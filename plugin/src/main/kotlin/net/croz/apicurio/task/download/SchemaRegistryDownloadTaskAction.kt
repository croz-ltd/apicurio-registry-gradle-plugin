package net.croz.apicurio.task.download

import io.apicurio.registry.rest.client.exception.RestClientException
import net.croz.apicurio.model.DownloadArtifact
import net.croz.apicurio.service.client.SchemaRegistryClient
import net.croz.apicurio.service.client.model.ClientCommand
import net.croz.apicurio.task.SchemaRegistryTaskAction
import org.gradle.api.logging.Logging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

/**
 * A task action which invokes the Apicurio Schema Registry client wrapper in order to download remote artifacts and write them to local files.
 * Used by [SchemaRegistryDownloadTask].
 *
 * @see SchemaRegistryDownloadTask
 */
internal class SchemaRegistryDownloadTaskAction(
    private val client: SchemaRegistryClient,
    private val rootDir: Path,
    private val artifacts: List<DownloadArtifact>
) : SchemaRegistryTaskAction {
    private val logger = Logging.getLogger(SchemaRegistryDownloadTaskAction::class.java)

    override fun run(): Int {
        var errorCount = 0

        artifacts.forEach {
            try {
                writeArtifactToFile(it)
            } catch (e: RestClientException) {
                logger.error("Could not retrieve artifact '${it.fullName}'", e)
                errorCount++
            }
        }

        return errorCount
    }

    private fun writeArtifactToFile(artifact: DownloadArtifact) {
        val outputDir = createParentDirectory(artifact)
        createAndWriteToFile(artifact, outputDir)
    }

    private fun createParentDirectory(artifact: DownloadArtifact): Path {
        val outputDir = rootDir.resolve(artifact.outputPath)
        outputDir.createDirectories()

        return outputDir
    }

    private fun createAndWriteToFile(
        artifact: DownloadArtifact,
        outputDir: Path
    ) {
        val command = ClientCommand.Download(artifact)

        val metadata = client.fetchMetadata(command)
        val fileName = artifact.outputFileName ?: metadata.name

        val outputFile = outputDir.resolve("${fileName}.${metadata.artifactType}")
        client.fetchContent(command).use {
            Files.write(outputFile, it.readAllBytes())
        }
    }
}
