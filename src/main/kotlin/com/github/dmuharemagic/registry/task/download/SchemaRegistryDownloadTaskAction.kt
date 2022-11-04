package com.github.dmuharemagic.registry.task.download

import com.github.dmuharemagic.registry.model.Action
import com.github.dmuharemagic.registry.service.client.SchemaRegistryClient
import com.github.dmuharemagic.registry.task.SchemaRegistryTaskAction
import io.apicurio.registry.rest.client.exception.RestClientException
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
    private val actionList: List<Action.Download>
) : SchemaRegistryTaskAction {
    private val logger = Logging.getLogger(SchemaRegistryDownloadTaskAction::class.java)

    override fun run(): Int {
        var errorCount = 0

        actionList.forEach {
            try {
                writeArtifactToFile(it)
            } catch (e: RestClientException) {
                logger.error("Could not retrieve artifact: '${it.artifact.fullName}'", e)
                errorCount++
            }
        }

        return errorCount
    }

    private fun writeArtifactToFile(command: Action.Download) {
        val outputDir = createParentDirectory(command)
        createAndWriteToFile(command, outputDir)
    }

    private fun createParentDirectory(command: Action.Download): Path {
        val outputDir = rootDir.resolve(command.outputPath)
        outputDir.createDirectories()

        return outputDir
    }

    private fun createAndWriteToFile(
        command: Action.Download,
        outputDir: Path
    ) {
        val artifact = command.toCommand()

        val metadata = client.fetchMetadata(artifact)
        val fileName = command.outputFileName ?: metadata.name

        val outputFile = outputDir.resolve("${fileName}.${metadata.artifactType.extension}")
        client.fetchContent(artifact).use {
            Files.write(outputFile, it.readAllBytes())
        }
    }
}