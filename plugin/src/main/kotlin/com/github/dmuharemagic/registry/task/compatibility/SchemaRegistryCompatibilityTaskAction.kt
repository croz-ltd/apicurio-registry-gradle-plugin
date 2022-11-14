package com.github.dmuharemagic.registry.task.compatibility

import com.github.dmuharemagic.registry.model.CompatibilityArtifact
import com.github.dmuharemagic.registry.service.client.SchemaRegistryClient
import com.github.dmuharemagic.registry.service.client.model.ClientCommand
import com.github.dmuharemagic.registry.task.SchemaRegistryTaskAction
import io.apicurio.registry.rest.client.exception.RestClientException
import org.gradle.api.logging.Logging
import java.nio.file.Path
import kotlin.io.path.inputStream

/**
 * A task action which invokes the Apicurio Schema Registry client wrapper in order to check for compatibility issues between local artifacts and their remote counterparts.
 * Used by [SchemaRegistryCompatibilityTask].
 *
 * @see SchemaRegistryCompatibilityTask
 */
internal class SchemaRegistryCompatibilityTaskAction(
    private val client: SchemaRegistryClient,
    private val rootDir: Path,
    private val artifacts: List<CompatibilityArtifact>
) : SchemaRegistryTaskAction {
    private val logger = Logging.getLogger(SchemaRegistryCompatibilityTaskAction::class.java)

    override fun run(): Int {
        var errorCount = 0

        artifacts.forEach {
            try {
                checkArtifactCompatibility(it)
            } catch (e: RestClientException) {
                logger.error("Could not check artifact compatibility '${it.fullName}'", e)
                errorCount++
            }
        }

        return errorCount
    }

    private fun checkArtifactCompatibility(artifact: CompatibilityArtifact) {
        val artifactPath = rootDir.resolve(artifact.path)

        val command = ClientCommand.Compatibility(artifact, artifactPath.inputStream())
        client.isCompatible(command)
    }
}