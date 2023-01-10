package net.croz.apicurio.core.specification

import io.apicurio.registry.types.ArtifactType as ClientArtifactType
import net.croz.apicurio.core.model.RetrieveArtifactMetadata
import net.croz.apicurio.core.testcontainers.ApicurioSchemaRegistryContainerFactory
import net.croz.apicurio.util.SchemaRegistryUtil
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

@Testcontainers
abstract class AbstractFunctionalSpecification extends Specification {
  @TempDir
  Path projectDir
  Path buildFile

  static final GenericContainer schemaRegistryContainer = ApicurioSchemaRegistryContainerFactory.makeAndStartContainer()

  def setup() {
    buildFile = Files.createFile(projectDir.resolve("build.gradle.kts"))
  }

  protected void assertArtifactExistsAndIsReadable(String path, String fileName) {
    def outputParentPath = projectDir.resolve(path)
    assert Files.exists(outputParentPath): "output file parent path must exist"

    def outputPath = outputParentPath.resolve(fileName)
    assert Files.exists(outputPath): "output file path must exist"
    assert Files.isReadable(outputPath): "output file must be readable"
  }

  protected void assertArtifactRegisteredProperly(String groupId = null, String artifactId, ClientArtifactType clientArtifactType) {
    def metadata = SchemaRegistryUtil.retrieveArtifactMetadata(schemaRegistryUrl, new RetrieveArtifactMetadata(groupId, artifactId))
    assert metadata: "metadata must be present"
    if (!groupId) {
      assert !metadata.groupId: "group ID should be default (null)"
    }
    else {
      assert metadata.groupId == groupId: "group ID must match"
    }
    assert metadata.id == artifactId: "artifact ID must match"
    assert metadata.type == clientArtifactType: "artifact type must match"
  }

  protected String getSchemaRegistryUrl() {
    "http://$schemaRegistryContainer.host:${schemaRegistryContainer.firstMappedPort}"
  }

  protected appendPluginDefinition() {
    buildFile << """
            plugins {
                id("net.croz.apicurio-registry-gradle-plugin")
            }
        """
  }

  protected BuildResult build(String... arguments) {
    createAndConfigureGradleRunner(arguments).build()
  }

  protected BuildResult buildAndFail(String... arguments) {
    createAndConfigureGradleRunner(arguments).buildAndFail()
  }

  private GradleRunner createAndConfigureGradleRunner(String... arguments) {
    GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectDir.toFile())
                .withArguments(arguments + '-s' as List<String>)
                .withDebug(true)
                .forwardOutput()
  }
}
