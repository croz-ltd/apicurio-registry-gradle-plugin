package com.github.dmuharemagic.registry.task.register

import com.github.dmuharemagic.registry.model.Action
import com.github.dmuharemagic.registry.service.client.SchemaRegistryClient
import com.github.dmuharemagic.registry.task.SchemaRegistryTaskAction
import io.apicurio.registry.rest.client.exception.RestClientException
import org.gradle.api.logging.Logging
import java.nio.file.Path
import kotlin.io.path.inputStream

/**
 * A task action which invokes the Apicurio Schema Registry client wrapper in order to register local artifacts on the Apicurio Schema Registry.
 * Used by [SchemaRegistryRegisterTask].
 *
 * @see SchemaRegistryRegisterTask
 */
internal class SchemaRegistryRegisterTaskAction(
    private val client: SchemaRegistryClient,
    private val rootDir: Path,
    private val actionList: List<Action.Register>
) : SchemaRegistryTaskAction {
    private val logger = Logging.getLogger(SchemaRegistryRegisterTaskAction::class.java)

    override fun run(): Int {
        var errorCount = 0

        actionList.forEach {
            try {
                registerArtifact(it)
            } catch (e: RestClientException) {
                logger.error("Could not register the artifact: '${it.artifact.fullName}'", e)
                errorCount++
            }
        }

        return errorCount
    }

    private fun registerArtifact(command: Action.Register) {
        val artifactPath = rootDir.resolve(command.path)

        val artifact = command.toCommand(artifactPath.inputStream())
        client.register(artifact)
    }
}