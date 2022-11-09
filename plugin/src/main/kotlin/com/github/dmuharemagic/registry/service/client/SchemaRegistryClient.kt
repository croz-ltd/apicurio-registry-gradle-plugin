package com.github.dmuharemagic.registry.service.client

import com.github.dmuharemagic.registry.model.Authentication
import com.github.dmuharemagic.registry.model.toArtifactType
import com.github.dmuharemagic.registry.model.toClientArtifactType
import com.github.dmuharemagic.registry.model.toClientConflictHandleType
import com.github.dmuharemagic.registry.service.SchemaRegistryClientService
import com.github.dmuharemagic.registry.service.client.model.ClientCommand
import com.github.dmuharemagic.registry.service.client.model.ClientMetadata
import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.client.RegistryClientFactory
import io.apicurio.registry.rest.v2.beans.IfExists
import io.apicurio.registry.types.ArtifactType
import io.apicurio.rest.client.auth.BasicAuth
import io.apicurio.rest.client.auth.OidcAuth
import io.apicurio.rest.client.auth.exception.AuthErrorHandler
import io.apicurio.rest.client.spi.ApicurioHttpClient
import io.apicurio.rest.client.spi.ApicurioHttpClientFactory
import java.io.InputStream

internal typealias ClientArtifactType = ArtifactType;
internal typealias ClientConflictHandleType = IfExists;

/**
 * A convenient wrapper around the Apicurio Schema Registry REST client consisting of common operations used by the defined tasks.
 * Used by the shared build service to integrate with the tasks provided by this plugin.
 *
 * @see SchemaRegistryClientService
 */
internal interface SchemaRegistryClient {
    /**
     * Retrieves the metadata correlated to a specific artifact with the information from the [command] object.
     *
     * @param command The command consisting of the necessary information needed to query the Apicurio Schema Registry for artifacts.
     *
     * @return the artifact metadata
     */
    fun fetchMetadata(command: ClientCommand.Download): ClientMetadata

    /**
     * Retrieves an input stream correlated to a specific artifact with the information from the [command] object.
     * This input stream is to be consumed by the download task for the purpose of saving a local copy of the file.
     *
     * @param command The command consisting of the necessary information needed to query the Apicurio Schema Registry for artifacts.
     *
     * @return an input stream to be consumed for manipulation
     */
    fun fetchContent(command: ClientCommand.Download): InputStream

    /**
     * Registers a new artifact with the information from the [command] object.
     *
     * @param command The command consisting of the necessary information needed by the Apicurio Schema Registry to register a new artifact.
     *
     * @return the created artifact's metadata
     */
    fun register(command: ClientCommand.Register): ClientMetadata

    /**
     * Validates that the local artifact file is compatible with its remote counterpart located on the Apicurio Schema Registry.
     *
     * @param command The command consisting of the necessary information needed to query the Apicurio Schema Registry for artifacts.
     *
     * @return the created artifact's metadata
     */
    fun isCompatible(command: ClientCommand.Compatibility)

    companion object : SchemaRegistryClient {
        private lateinit var httpClient: ApicurioHttpClient
        private lateinit var client: RegistryClient

        @Volatile
        private var INSTANCE: SchemaRegistryClient? = null

        fun getInstance(
            url: String,
            authentication: Authentication
        ): SchemaRegistryClient =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildClient(url, authentication).also { INSTANCE = it }
            }

        /**
         * Attemps to clean-up opened resources.
         */
        fun close() {
            if (Companion::httpClient.isInitialized) {
                httpClient.close()
            }
            if (Companion::client.isInitialized) {
                client.close()
            }
        }

        /**
         * Builds a new singleton instance of this wrapper utilizing the provided schema registry URL and the authentication details.
         */
        private fun buildClient(
            url: String,
            authentication: Authentication
        ): SchemaRegistryClient {
            when (authentication) {
                is Authentication.None -> {
                    client = RegistryClientFactory.create(url)
                }

                is Authentication.Basic -> {
                    val auth = BasicAuth(authentication.username, authentication.password)
                    client = RegistryClientFactory.create(url, emptyMap(), auth)
                }

                is Authentication.OAuth -> {
                    httpClient =
                        ApicurioHttpClientFactory.create(authentication.authServerUrl, AuthErrorHandler())
                    val auth = OidcAuth(
                        httpClient,
                        authentication.clientId,
                        authentication.clientSecret
                    )
                    client = RegistryClientFactory.create(url, emptyMap(), auth)
                }
            }

            return this@Companion
        }

        override fun fetchMetadata(command: ClientCommand.Download): ClientMetadata {
            val artifact = command.artifact

            return if (artifact.version == null) {
                val metadata =
                    client.getArtifactMetaData(artifact.groupId, artifact.id)

                ClientMetadata(metadata.name, metadata.type.toArtifactType())
            } else {
                val metadata =
                    client.getArtifactVersionMetaData(
                        artifact.groupId,
                        artifact.id,
                        artifact.version
                    )

                ClientMetadata(metadata.name, metadata.type.toArtifactType())
            }
        }

        override fun fetchContent(command: ClientCommand.Download): InputStream {
            val artifact = command.artifact

            return if (artifact.version == null) {
                client.getLatestArtifact(artifact.groupId, artifact.id)
            } else {
                client.getArtifactVersion(
                    artifact.groupId,
                    artifact.id,
                    artifact.version
                )
            }
        }

        override fun register(command: ClientCommand.Register): ClientMetadata {
            val artifact = command.artifact

            val createArtifactMetadata = client.createArtifact(
                artifact.groupId,
                artifact.id,
                artifact.version,
                artifact.artifactType.toClientArtifactType(),
                artifact.conflictHandleType.toClientConflictHandleType(),
                artifact.canonicalize,
                artifact.name,
                artifact.description,
                command.data
            )

            return ClientMetadata(createArtifactMetadata.name, createArtifactMetadata.type.toArtifactType())
        }

        override fun isCompatible(command: ClientCommand.Compatibility) {
            val artifact = command.artifact

            client.testUpdateArtifact(artifact.groupId, artifact.id, command.data)
        }
    }
}