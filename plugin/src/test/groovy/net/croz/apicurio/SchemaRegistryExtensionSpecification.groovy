package net.croz.apicurio

import net.croz.apicurio.extension.CompatibilityHandler
import net.croz.apicurio.extension.ConfigurationHandler
import net.croz.apicurio.extension.DownloadHandler
import net.croz.apicurio.extension.RegisterHandler
import net.croz.apicurio.model.*
import net.croz.apicurio.util.ArtifactMetadataGeneratingUtil
import org.gradle.api.Action
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SchemaRegistryExtensionSpecification extends Specification {
  private SchemaRegistryExtension extension = new SchemaRegistryExtension(ProjectBuilder.builder().build().objects)

  def "should set schema registry URL through configuration handler"() {
    given:
        def url = "localhost:8080"

    when:
        extension.config(new Action<ConfigurationHandler>() {
          @Override
          void execute(ConfigurationHandler handler) {
            handler.url(url)
          }
        })

    then:
        extension.config$plugin.url$plugin.get() == url
  }

  def "should set basic authentication details through configuration handler"() {
    given:
        def username = "test"
        def password = "password"

    when:
        extension.config(new Action<ConfigurationHandler>() {
          @Override
          void execute(ConfigurationHandler handler) {
            handler.auth(username, password)
          }
        })

    then:
        Authentication authenticationContext = extension.config$plugin.authentication$plugin.get()
        authenticationContext
        authenticationContext instanceof Authentication.Basic
        def castedAuthenticationContext = authenticationContext as Authentication.Basic
        castedAuthenticationContext.username == username
        castedAuthenticationContext.password == password
  }

  def "should set OAuth authentication details through configuration handler"() {
    given:
        def authServerUrl = "http://localhost:8080"
        def clientId = UUID.randomUUID().toString()
        def clientSecret = UUID.randomUUID().toString()

    when:
        extension.config(new Action<ConfigurationHandler>() {
          @Override
          void execute(ConfigurationHandler handler) {
            handler.auth(authServerUrl, clientId, clientSecret)
          }
        })

    then:
        Authentication authenticationContext = extension.config$plugin.authentication$plugin.get()
        authenticationContext
        authenticationContext instanceof Authentication.OAuth
        def castedAuthenticationContext = authenticationContext as Authentication.OAuth
        castedAuthenticationContext.authServerUrl == authServerUrl
        castedAuthenticationContext.clientId == clientId
        castedAuthenticationContext.clientSecret == clientSecret
  }

  def "should set artifact details to be downloaded through download handler"() {
    given:
        def metadata = ArtifactMetadataGeneratingUtil.generate()

    when:
        extension.download(new Action<DownloadHandler>() {
          @Override
          void execute(DownloadHandler handler) {
            handler.artifact(new Action<DownloadArtifact>() {
              @Override
              void execute(DownloadArtifact artifact) {
                artifact.groupId = metadata.groupId
                artifact.id = metadata.artifactId
                artifact.version = metadata.version
                artifact.outputPath = metadata.outputPath
              }
            })
          }
        })

    then:
        def artifactCommandList = extension.download$plugin.artifacts$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
          it.id == metadata.artifactId
          it.groupId == metadata.groupId
          it.version == metadata.version
          it.outputPath == metadata.outputPath
          !it.outputFileName
        }
  }

  def "should set artifacts details for specific group to be registered through register handler"(String conflictHandleType) {
    given:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def description = "description"
        def artifactTypeName = "AVRO"
        def canonicalize = false

    when:
        extension.register(new Action<RegisterHandler>() {
          @Override
          void execute(RegisterHandler handler) {
            handler.artifact(new Action<RegisterArtifact>() {
              @Override
              void execute(RegisterArtifact artifact) {
                artifact.groupId = metadata.groupId
                artifact.id = metadata.artifactId
                artifact.name = metadata.name
                artifact.type = artifactTypeName
                artifact.path = metadata.outputPath
                artifact.description = description
                artifact.version = metadata.version
                artifact.canonicalize = canonicalize
                artifact.conflictHandleType = conflictHandleType
              }
            })
          }
        })

    then:
        def artifactCommandList = extension.register$plugin.artifacts$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
          it.id == metadata.artifactId
          it.groupId === metadata.groupId
          it.name == metadata.name
          it.type == artifactTypeName
          it.path == metadata.outputPath
          it.description == description
          it.version == metadata.version
          it.canonicalize == canonicalize
          it.conflictHandleType == conflictHandleType ?: ConflictHandleType.FAIL
        }

    where:
        conflictHandleType                         | _
        ConflictHandleType.RETURN_OR_UPDATE.name() | _
        null                                       | _
  }

  def "should set artifacts details for default group to be registered through register handler"(String conflictHandleType) {
    given:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def description = "description"
        def artifactTypeName = "AVRO"
        def canonicalize = false

    when:
        extension.register(new Action<RegisterHandler>() {
          @Override
          void execute(RegisterHandler handler) {
            handler.artifact(new Action<RegisterArtifact>() {
              @Override
              void execute(RegisterArtifact artifact) {
                artifact.id = metadata.artifactId
                artifact.name = metadata.name
                artifact.type = artifactTypeName
                artifact.path = metadata.outputPath
                artifact.description = description
                artifact.version = metadata.version
                artifact.canonicalize = canonicalize
                artifact.conflictHandleType = conflictHandleType
              }
            })
          }
        })

    then:
        def artifactCommandList = extension.register$plugin.artifacts$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
          !it.groupId
          it.id == metadata.artifactId
          it.name == metadata.name
          it.type == artifactTypeName
          it.description == description
          it.version == metadata.version
          it.canonicalize == canonicalize
          it.conflictHandleType == conflictHandleType ?: ConflictHandleType.FAIL
          it.path == metadata.outputPath
        }

    where:
        conflictHandleType                         | _
        ConflictHandleType.RETURN_OR_UPDATE.name() | _
        null                                       | _
  }

  def "should set artifact details for specific group to check for compatibility issues through compatibility handler"() {
    given:
        def metadata = ArtifactMetadataGeneratingUtil.generate()

    when:
        extension.compatibility(new Action<CompatibilityHandler>() {
          @Override
          void execute(CompatibilityHandler handler) {
            handler.artifact(new Action<CompatibilityArtifact>() {
              @Override
              void execute(CompatibilityArtifact artifact) {
                artifact.groupId = metadata.groupId
                artifact.id = metadata.artifactId
                artifact.path = metadata.outputPath
              }
            })
          }
        })

    then:
        def artifactCommandList = extension.compatibility$plugin.artifacts$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
          it.id == metadata.artifactId
          it.groupId == metadata.groupId
          it.path == metadata.outputPath
        }
  }

  def "should set artifact details for default group to check for compatibility issues through compatibility handler"() {
    given:
        def metadata = ArtifactMetadataGeneratingUtil.generate()

    when:
        extension.compatibility(new Action<CompatibilityHandler>() {
          @Override
          void execute(CompatibilityHandler handler) {
            handler.artifact(new Action<CompatibilityArtifact>() {
              @Override
              void execute(CompatibilityArtifact artifact) {
                artifact.id = metadata.artifactId
                artifact.path = metadata.outputPath
              }
            })
          }
        })

    then:
        def artifactCommandList = extension.compatibility$plugin.artifacts$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
          !it.groupId
          it.id == metadata.artifactId
          it.path == metadata.outputPath
        }
  }
}
