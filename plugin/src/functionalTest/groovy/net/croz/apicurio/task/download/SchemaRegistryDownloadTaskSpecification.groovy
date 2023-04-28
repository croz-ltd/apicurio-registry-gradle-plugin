package net.croz.apicurio.task.download

import net.croz.apicurio.core.model.RegisterArtifact
import net.croz.apicurio.core.specification.AbstractFunctionalSpecification
import net.croz.apicurio.util.ArtifactMetadataGeneratingUtil
import net.croz.apicurio.util.SchemaRegistryUtil
import org.gradle.testkit.runner.TaskOutcome

class SchemaRegistryDownloadTaskSpecification extends AbstractFunctionalSpecification {
  def setup() {
    appendPluginDefinition()
  }

  def "should fail downloading non-existent unversioned artifact"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                download {
                    artifact {
                        id = "$metadata.artifactId"
                        outputPath = "$metadata.outputPath"
                    }
                }
            }
        """

    when:
        def result = buildAndFail(SchemaRegistryDownloadTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryDownloadTask.TASK_NAME").outcome == TaskOutcome.FAILED
        result.output.contains("1 artifacts not downloaded, see logs for details")
  }

  def "should fail downloading non-existent versioned artifact"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate()
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                download {
                    artifact {
                        id = "$metadata.artifactId"
                        outputPath = "$metadata.outputPath"
                        version = "$metadata.version"
                    }
                }
            }
        """

    when:
        def result = buildAndFail(SchemaRegistryDownloadTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryDownloadTask.TASK_NAME").outcome == TaskOutcome.FAILED
        result.output.contains("1 artifacts not downloaded, see logs for details")
  }

  def "should download an existing unversioned artifact"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate(artifactType)
        SchemaRegistryUtil.registerArtifact(schemaRegistryUrl,
                                            new RegisterArtifact(path: inputFileName, artifactId: metadata.artifactId, name: metadata.name, type: artifactType))
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                download {
                    artifact {
                        id = "$metadata.artifactId"
                        outputPath = "$metadata.outputPath"
                    }
                }
            }
        """

    when:
        def result = build(SchemaRegistryDownloadTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryDownloadTask.TASK_NAME").outcome == TaskOutcome.SUCCESS
        assertArtifactExistsAndIsReadable(metadata.outputPath, "${metadata.name}.${artifactType}")

    where:
        inputFileName   | artifactType
        "TestAvro.avsc" | "AVRO"
  }

  def "should download an existing versioned artifact"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate(artifactType)
        SchemaRegistryUtil.registerArtifact(schemaRegistryUrl, new RegisterArtifact(path: inputFileName, artifactId: metadata.artifactId, name: metadata.name, version: metadata.version, type: artifactType))
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                download {
                    artifact {
                        id = "$metadata.artifactId"
                        version = "$metadata.version"
                        outputPath = "$metadata.outputPath"
                    }
                }
            }
        """

    when:
        def result = build(SchemaRegistryDownloadTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryDownloadTask.TASK_NAME").outcome == TaskOutcome.SUCCESS
        assertArtifactExistsAndIsReadable(metadata.outputPath, "${metadata.name}.${artifactType}")

    where:
        inputFileName   | artifactType
        "TestAvro.avsc" | "AVRO"
  }

  def "should download artifact with custom output file name"() {
    setup:
        def metadata = ArtifactMetadataGeneratingUtil.generate(artifactType)
        SchemaRegistryUtil.registerArtifact(schemaRegistryUrl,
                                            new RegisterArtifact(path: inputFileName, artifactId: metadata.artifactId, name: metadata.name, type: artifactType))
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                download {
                    artifact {
                        id = "$metadata.artifactId"
                        outputPath = "$metadata.outputPath"
                        outputFileName = "$outputFileName"
                    }
                }
            }
        """

    when:
        def result = build(SchemaRegistryDownloadTask.TASK_NAME)

    then:
        result.task(":$SchemaRegistryDownloadTask.TASK_NAME").outcome == TaskOutcome.SUCCESS
        assertArtifactExistsAndIsReadable(metadata.outputPath, "${outputFileName}.${artifactType}")

    where:
        inputFileName   | outputFileName   | artifactType
        "TestAvro.avsc" | "CustomTestAvro" | "AVRO"
  }
}
