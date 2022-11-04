package com.github.dmuharemagic.registry.task.compatibility

import com.github.dmuharemagic.registry.core.model.RegisterArtifact
import com.github.dmuharemagic.registry.core.specification.AbstractFunctionalSpecification
import com.github.dmuharemagic.registry.model.ArtifactType
import com.github.dmuharemagic.registry.model.ArtifactTypeKt
import com.github.dmuharemagic.registry.util.ArtifactMetadataGeneratingUtil
import com.github.dmuharemagic.registry.util.IOUtil
import com.github.dmuharemagic.registry.util.SchemaRegistryUtil
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
                    artifact(artifactId = "$metadata.artifactId", path = "${artifactFile.toAbsolutePath().toString()}")
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
        SchemaRegistryUtil.registerArtifact(schemaRegistryUrl, new RegisterArtifact(path: inputFileName, artifactId: metadata.artifactId, name: metadata.name, type: ArtifactTypeKt.toClientArtifactType(ArtifactType.@Companion.fromName(artifactTypeName))))
        def artifactFile = projectDir.resolve(inputFileName)
        Files.createFile(artifactFile) << IOUtil.getResource(inputFileName)
        buildFile << """
            schemaRegistry {
                config {
                    url("$schemaRegistryUrl")
                }
                compatibility {
                    artifact(artifactId = "$metadata.artifactId", path = "${artifactFile.toAbsolutePath().toString()}")
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
