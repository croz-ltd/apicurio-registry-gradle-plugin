package net.croz.apicurio.task.compatibility

import net.croz.apicurio.core.model.RegisterArtifact
import net.croz.apicurio.core.specification.AbstractFunctionalSpecification
import net.croz.apicurio.util.ArtifactMetadataGeneratingUtil
import net.croz.apicurio.util.IOUtil
import net.croz.apicurio.util.SchemaRegistryUtil
import org.gradle.testkit.runner.TaskOutcome

import java.nio.file.Files

class SchemaRegistryCompatibilityTaskSpecification extends AbstractFunctionalSpecification {
  def setup() {
    appendPluginDefinition()
  }

  def "should fail checking for compatibility issues of a non-existent artifact"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def artifactFile = projectDir.resolve(inputFileName)
        Files.createFile(artifactFile) << IOUtil.getResource(inputFileName)
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                compatibility {
                    artifact {
                        id = "$metadata.artifactId"
                        path = "${artifactFile.toAbsolutePath().toString()}"
                    }
                }
            }
        """

    when:
        def result = buildAndFail(SchemaRegistryCompatibilityTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryCompatibilityTask.TASK_NAME").outcome == TaskOutcome.FAILED
        result.output.contains("1 artifacts not compatible, see logs for details")

    where:
        inputFileName   | _
        "TestAvro.avsc" | _
  }

  def "should check artifact compatibility"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        SchemaRegistryUtil.registerArtifact(schemaRegistryUrl, new RegisterArtifact(path: inputFileName, artifactId: metadata.artifactId, name: metadata.name, type: artifactTypeName))
        def artifactFile = projectDir.resolve(inputFileName)
        Files.createFile(artifactFile) << IOUtil.getResource(inputFileName)
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                compatibility {
                    artifact {
                        id = "$metadata.artifactId"
                        path = "${artifactFile.toAbsolutePath().toString()}"
                    }
                }
            }
        """

    when:
        def result = build(SchemaRegistryCompatibilityTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryCompatibilityTask.TASK_NAME").outcome == TaskOutcome.SUCCESS

    where:
        inputFileName   | artifactTypeName
        "TestAvro.avsc" | "AVRO"
  }
}
