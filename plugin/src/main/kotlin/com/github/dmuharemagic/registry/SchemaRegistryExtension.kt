package com.github.dmuharemagic.registry

import com.github.dmuharemagic.registry.extension.CompatibilityHandler
import com.github.dmuharemagic.registry.extension.ConfigurationHandler
import com.github.dmuharemagic.registry.extension.DownloadHandler
import com.github.dmuharemagic.registry.extension.RegisterHandler
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * The top-level DSL structure.
 *
 * @see ConfigurationHandler
 * @see DownloadHandler
 * @see RegisterHandler
 * @see CompatibilityHandler
 */
open class SchemaRegistryExtension @Inject constructor(factory: ObjectFactory) {
    internal companion object {
        internal const val EXTENSION_NAME = "schemaRegistry"

        internal fun Project.schemaRegistry(): SchemaRegistryExtension =
            extensions.create(EXTENSION_NAME, SchemaRegistryExtension::class.java)
    }

    internal val config: ConfigurationHandler =
        factory.newInstance(ConfigurationHandler::class.java)

    internal val download: DownloadHandler =
        factory.newInstance(DownloadHandler::class.java)

    internal val register: RegisterHandler =
        factory.newInstance(RegisterHandler::class.java)

    internal val compatibility: CompatibilityHandler =
        factory.newInstance(CompatibilityHandler::class.java)

    /**
     * Customize the connection information for the schema registry. See [ConfigurationHandler] for more details.
     */
    fun config(action: Action<ConfigurationHandler>) = action.execute(config)

    /**
     * Customize the artifact details which are needed for downloading the remote artifacts. See [DownloadHandler] for more details.
     */
    fun download(action: Action<DownloadHandler>) = action.execute(download)

    /**
     * Customize the artifact details which are needed for registering the local artifacts to the schema registry. See [RegisterHandler] for more details.
     */
    fun register(action: Action<RegisterHandler>) = action.execute(register)

    /**
     * Customize the artifact details which are needed for checking the local artifact compatibility with their remote counterparts in the schema registry. See [CompatibilityHandler] for more details.
     */
    fun compatibility(action: Action<CompatibilityHandler>) = action.execute(compatibility)

}