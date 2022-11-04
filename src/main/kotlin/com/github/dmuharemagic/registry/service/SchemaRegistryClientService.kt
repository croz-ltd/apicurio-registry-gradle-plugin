package com.github.dmuharemagic.registry.service

import com.github.dmuharemagic.registry.SchemaRegistryExtension
import com.github.dmuharemagic.registry.model.Authentication
import com.github.dmuharemagic.registry.service.client.SchemaRegistryClient
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

/**
 * A shared build service holding the REST client wrapper instance that is used for the network operations with the Apicurio Schema Registry.
 */
internal abstract class SchemaRegistryClientService : BuildService<SchemaRegistryClientService.Params>, AutoCloseable {
    companion object {
        private const val NAME = "schemaRegistryClientService"

        internal fun register(project: Project, extension: SchemaRegistryExtension) =
            project.gradle.sharedServices.registerIfAbsent(NAME, SchemaRegistryClientService::class.java) {
                it.parameters.url.set(extension.config.url)
                it.parameters.authentication.set(extension.config.authentication)
            }
    }

    internal interface Params : BuildServiceParameters {
        val url: Property<String>
        val authentication: Property<Authentication>
    }

    internal val client: SchemaRegistryClient

    init {
        val url = parameters.url.get()
        val authenticationContext = parameters.authentication.get()

        client = SchemaRegistryClient.getInstance(url, authenticationContext)
    }

    override fun close() {
        SchemaRegistryClient.close()
    }
}