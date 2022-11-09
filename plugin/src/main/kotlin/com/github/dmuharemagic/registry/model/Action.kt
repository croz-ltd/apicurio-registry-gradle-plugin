package com.github.dmuharemagic.registry.model

import com.github.dmuharemagic.registry.service.client.model.ClientCommand
import com.github.dmuharemagic.registry.task.compatibility.SchemaRegistryCompatibilityTask
import com.github.dmuharemagic.registry.task.compatibility.SchemaRegistryCompatibilityTaskAction
import com.github.dmuharemagic.registry.task.download.SchemaRegistryDownloadTask
import com.github.dmuharemagic.registry.task.download.SchemaRegistryDownloadTaskAction
import com.github.dmuharemagic.registry.task.register.SchemaRegistryRegisterTask
import com.github.dmuharemagic.registry.task.register.SchemaRegistryRegisterTaskAction
import java.io.InputStream

/**
 * A sealed class incorporating different types of commands produced by the tasks defined by this plugin.
 *
 * @property artifact The artifact information. See [Artifact] for more details.
 *
 * @see SchemaRegistryDownloadTask
 * @see SchemaRegistryRegisterTask
 * @see SchemaRegistryCompatibilityTask
 */
internal sealed class Action(open val artifact: Artifact) {
    /**
     * Represents an action containing the information used by [SchemaRegistryDownloadTask] for downloading remote artifacts from the Apicurio Schema Registry.
     *
     * @property artifact The artifact information. See [Artifact] for more details.
     * @property outputPath The directory to where the remote file should be downloaded to.
     * @property outputFileName Customizable file name of the remote file to be downloaded. The artifact name fetched from the schema registry is applied if omitted.
     *
     * @see SchemaRegistryDownloadTask
     * @see SchemaRegistryDownloadTaskAction
     */
    data class Download(
        override val artifact: Artifact.Basic,
        val outputPath: String,
        val outputFileName: String? = null
    ) : Action(artifact) {
        /**
         * Maps the action data to the [ClientCommand.Download] command.
         */
        internal fun toCommand() = ClientCommand.Download(artifact)
    }

    /**
     * Represents an action containing the information used by [SchemaRegistryRegisterTask] for registering local artifacts on the Apicurio Schema Registry.
     *
     * @property artifact The artifact information. See [Artifact] for more details.
     * @property path The directory where the local artifact file is located in.
     *
     * @see SchemaRegistryRegisterTask
     * @see SchemaRegistryRegisterTaskAction
     */
    data class Register(
        override val artifact: Artifact.Full,
        val path: String,
    ) : Action(artifact) {
        /**
         * Maps the action data to the [ClientCommand.Register] command and enriches it with the local artifact content.
         */
        internal fun toCommand(data: InputStream) = ClientCommand.Register(artifact, data)
    }

    /**
     * Represents an action containing the information used by [SchemaRegistryCompatibilityTask] for checking the compatibility between the local artifacts and their remote counterparts registered on the Apicurio Schema Registry.
     *
     * @property artifact The artifact information. See [Artifact] for more details.
     * @property path The directory where the local artifact file is located in.
     *
     * @see SchemaRegistryCompatibilityTask
     * @see SchemaRegistryCompatibilityTaskAction
     */
    data class Compatibility(
        override val artifact: Artifact.Basic,
        val path: String,
    ) : Action(artifact) {
        /**
         * Maps the action data to the [ClientCommand.Compatibility] command and enriches it with the local artifact content.
         */
        internal fun toCommand(data: InputStream) = ClientCommand.Compatibility(artifact, data)
    }
}