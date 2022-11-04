package com.github.dmuharemagic.registry.task.compatibility

import com.github.dmuharemagic.registry.model.Action
import com.github.dmuharemagic.registry.service.client.SchemaRegistryClient
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
    private val actionList: List<Action.Compatibility>
) : SchemaRegistryTaskAction {
    private val logger = Logging.getLogger(SchemaRegistryCompatibilityTaskAction::class.java)

    override fun run(): Int {
        var errorCount = 0

        actionList.forEach {
            try {
                checkArtifactCompatibility(it)
            } catch (e: RestClientException) {
                logger.error("Could not check compatibility for artifact: '${it.artifact.fullName}'", e)
                errorCount++
            }
        }

        return errorCount
    }

    private fun checkArtifactCompatibility(command: Action.Compatibility) {
        val artifactPath = rootDir.resolve(command.path)

        val artifact = command.toCommand(artifactPath.inputStream())
        client.isCompatible(artifact)
    }
}