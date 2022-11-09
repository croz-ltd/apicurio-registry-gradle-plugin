package com.github.dmuharemagic.registry

import com.github.dmuharemagic.registry.extension.CompatibilityHandler
import com.github.dmuharemagic.registry.extension.ConfigurationHandler
import com.github.dmuharemagic.registry.extension.DownloadHandler
import com.github.dmuharemagic.registry.extension.RegisterHandler
import com.github.dmuharemagic.registry.model.Authentication
import com.github.dmuharemagic.registry.model.ConflictHandleType
import com.github.dmuharemagic.registry.util.ArtifactMetadataGeneratingUtil
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class SchemaRegistryExtensionSpecification extends Specification {
    @Shared
    private Project project = ProjectBuilder.builder().build()

    private SchemaRegistryExtension extension

    def setup() {
        extension = new SchemaRegistryExtension(project.objects)
    }

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
                handler.artifact(metadata.groupId, metadata.artifactId, metadata.version, metadata.outputPath, null)
            }
        })

        then:
        def artifactCommandList = extension.download$plugin.actionList$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
            it.artifact.id == metadata.artifactId
            it.artifact.groupId === metadata.groupId
            it.artifact.version == metadata.version
            it.outputPath == metadata.outputPath
            !it.outputFileName
        }
    }

    def "should set artifacts details for specific group to be registered through register handler"(String conflictHandlerType) {
        given:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def description = "description"
        def artifactTypeName = "AVRO"
        def canonicalize = false

        when:
        extension.register(new Action<RegisterHandler>() {
            @Override
            void execute(RegisterHandler handler) {
                handler.artifact(metadata.groupId, metadata.artifactId, metadata.name, artifactTypeName, metadata.outputPath, description, metadata.version, canonicalize, conflictHandlerType)
            }
        })

        then:
        def artifactCommandList = extension.register$plugin.actionList$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
            it.artifact.id == metadata.artifactId
            it.artifact.groupId === metadata.groupId
            it.artifact.name == metadata.name
            it.artifact.artifactType.name() == artifactTypeName
            it.path == metadata.outputPath
            it.artifact.description == description
            it.artifact.version == metadata.version
            it.artifact.canonicalize == canonicalize
            it.artifact.conflictHandleType.name() == conflictHandlerType ?: ConflictHandleType.FAIL
        }

        where:
        conflictHandlerType                        | _
        ConflictHandleType.RETURN_OR_UPDATE.name() | _
        null                                       | _
    }

    def "should set artifacts details for default group to be registered through register handler"(String conflictHandlerType) {
        given:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def description = "description"
        def artifactTypeName = "AVRO"
        def canonicalize = false

        when:
        extension.register(new Action<RegisterHandler>() {
            @Override
            void execute(RegisterHandler handler) {
                handler.artifact(metadata.artifactId, metadata.name, artifactTypeName, metadata.outputPath, description, metadata.version, canonicalize, conflictHandlerType)
            }
        })

        then:
        def artifactCommandList = extension.register$plugin.actionList$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
            it.artifact.id == metadata.artifactId
            !it.artifact.groupId
            it.artifact.name == metadata.name
            it.artifact.artifactType.name() == artifactTypeName
            it.artifact.description == description
            it.artifact.version == metadata.version
            it.artifact.canonicalize == canonicalize
            it.artifact.conflictHandleType.name() == conflictHandlerType ?: ConflictHandleType.FAIL
            it.path == metadata.outputPath
        }

        where:
        conflictHandlerType                        | _
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
                handler.artifact(metadata.groupId, metadata.artifactId, metadata.outputPath)
            }
        })

        then:
        def artifactCommandList = extension.compatibility$plugin.actionList$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
            it.artifact.id == metadata.artifactId
            it.artifact.groupId == metadata.groupId
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
                handler.artifact(metadata.artifactId, metadata.outputPath)
            }
        })

        then:
        def artifactCommandList = extension.compatibility$plugin.actionList$plugin.get()
        artifactCommandList != null
        artifactCommandList.size() == 1
        artifactCommandList.every {
            it.artifact.id == metadata.artifactId
            !it.artifact.groupId
            it.path == metadata.outputPath
        }
    }
}