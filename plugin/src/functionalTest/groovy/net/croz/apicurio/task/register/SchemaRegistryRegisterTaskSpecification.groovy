package net.croz.apicurio.task.register

import io.apicurio.registry.types.ArtifactType as ClientArtifactType
import net.croz.apicurio.core.specification.AbstractFunctionalSpecification
import net.croz.apicurio.util.ArtifactMetadataGeneratingUtil
import net.croz.apicurio.util.IOUtil
import org.gradle.testkit.runner.TaskOutcome

import java.nio.file.Files

class SchemaRegistryRegisterTaskSpecification extends AbstractFunctionalSpecification {
  def setup() {
    appendPluginDefinition()
    Files.createDirectories(projectDir.resolve("src").resolve("main").resolve("artifact"))
  }

  def "should fail registering non-existent local artifact"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def artifactTypeName = "AVRO"
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                register {
                    artifact {
                        id = "$metadata.artifactId"
                        name = "$metadata.name"
                        type = "$artifactTypeName"
                        path = "$metadata.outputPath"
                    }
                }
            }
        """

    when:
        def result = buildAndFail(SchemaRegistryRegisterTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryRegisterTask.TASK_NAME").outcome == TaskOutcome.FAILED
        result.output.contains("1 artifacts not registered, see logs for details")
  }

  def "should register local artifact in default group"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def artifactFile = projectDir.resolve(inputFileName)
        Files.createFile(artifactFile) << IOUtil.getResource(inputFileName)
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                register {
                    artifact {
                        id = "$metadata.artifactId"
                        name = "$metadata.name"
                        type = "$artifactTypeName"
                        path = "${artifactFile.toAbsolutePath().toString()}"
                    }
                }
            }
        """

    when:
        def result = build(SchemaRegistryRegisterTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryRegisterTask.TASK_NAME").outcome == TaskOutcome.SUCCESS
        assertArtifactRegisteredProperly metadata.artifactId, artifactTypeName

    where:
        inputFileName   | artifactTypeName
        "TestAvro.avsc" | "AVRO"
  }

  def "should register local artifact in specific group"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        def artifactFile = projectDir.resolve(inputFileName)
        Files.createFile(artifactFile) << IOUtil.getResource(inputFileName)
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                register {
                    artifact {
                        groupId = "$metadata.groupId"
                        id = "$metadata.artifactId"
                        name = "$metadata.name"
                        type = "$artifactTypeName"
                        path = "${artifactFile.toAbsolutePath().toString()}"
                        conflictHandleType = "$conflictHandleTypeName"
                    }
                }
            }
        """

    when:
        def result = build(SchemaRegistryRegisterTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryRegisterTask.TASK_NAME").outcome == TaskOutcome.SUCCESS
        assertArtifactRegisteredProperly metadata.groupId, metadata.artifactId, artifactTypeName

    where:
        inputFileName   | artifactTypeName | conflictHandleTypeName
        "TestAvro.avsc" | "AVRO"           | "RETURN_OR_UPDATE"
  }
}
