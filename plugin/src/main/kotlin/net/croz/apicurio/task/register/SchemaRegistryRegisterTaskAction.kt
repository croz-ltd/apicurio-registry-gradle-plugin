package net.croz.apicurio.task.register

import io.apicurio.registry.rest.client.exception.RestClientException
import net.croz.apicurio.model.RegisterArtifact
import net.croz.apicurio.service.client.SchemaRegistryClient
import net.croz.apicurio.service.client.model.ClientCommand
import net.croz.apicurio.task.SchemaRegistryTaskAction
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
    private val artifacts: List<RegisterArtifact>
) : SchemaRegistryTaskAction {
    private val logger = Logging.getLogger(SchemaRegistryRegisterTaskAction::class.java)

    override fun run(): Int {
        var errorCount = 0

        artifacts.forEach {
            try {
                registerArtifact(it)
            } catch (e: RestClientException) {
                logger.error("Could not register artifact '${it.fullName}'", e)
                errorCount++
            }
        }

        return errorCount
    }

    private fun registerArtifact(artifact: RegisterArtifact) {
        val artifactPath = rootDir.resolve(artifact.path)

        val command = ClientCommand.Register(artifact, artifactPath.inputStream())
        client.register(command)
    }
}