package net.croz.apicurio.service.client

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.client.RegistryClientFactory
import io.apicurio.registry.rest.v2.beans.IfExists
import io.apicurio.rest.client.auth.BasicAuth
import io.apicurio.rest.client.auth.OidcAuth
import io.apicurio.rest.client.auth.exception.AuthErrorHandler
import io.apicurio.rest.client.spi.ApicurioHttpClient
import io.apicurio.rest.client.spi.ApicurioHttpClientFactory
import net.croz.apicurio.model.ArtifactType
import net.croz.apicurio.model.Authentication
import net.croz.apicurio.model.ConflictHandleType
import net.croz.apicurio.model.toArtifactType
import net.croz.apicurio.model.toClientArtifactType
import net.croz.apicurio.model.toClientConflictHandleType
import net.croz.apicurio.service.SchemaRegistryClientService
import net.croz.apicurio.service.client.model.ClientCommand
import net.croz.apicurio.service.client.model.ClientMetadata
import java.io.InputStream

internal typealias ClientArtifactType = io.apicurio.registry.types.ArtifactType
internal typealias ClientConflictHandleType = IfExists

/**
 * A convenient wrapper around the Apicurio Schema Registry REST client consisting of common operations used by the defined tasks.
 * Used by the shared build service to integrate with the tasks provided by this plugin.
 *
 * @property url schema registry URL
 * @property authentication authentication details
 *
 * @see SchemaRegistryClientService
 */
internal class SchemaRegistryClient(url: String, authentication: Authentication) {
    private var client: RegistryClient
    private lateinit var httpClient: ApicurioHttpClient

    init {
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
    }

    /**
     * Retrieves the metadata correlated to a specific artifact with the information from the [command] object.
     *
     * @param command The command consisting of the necessary information needed to query the Apicurio Schema Registry for artifacts.
     *
     * @return the artifact metadata
     */
    fun fetchMetadata(command: ClientCommand.Download): ClientMetadata {
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

    /**
     * Retrieves an input stream correlated to a specific artifact with the information from the [command] object.
     * This input stream is to be consumed by the download task for the purpose of saving a local copy of the file.
     *
     * @param command The command consisting of the necessary information needed to query the Apicurio Schema Registry for artifacts.
     *
     * @return an input stream to be consumed for manipulation
     */
    fun fetchContent(command: ClientCommand.Download): InputStream {
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

    /**
     * Registers a new artifact with the information from the [command] object.
     *
     * @param command The command consisting of the necessary information needed by the Apicurio Schema Registry to register a new artifact.
     *
     * @return the created artifact's metadata
     */
    fun register(command: ClientCommand.Register): ClientMetadata {
        val artifact = command.artifact

        val artifactType = ArtifactType.fromName(artifact.type)
        val conflictHandleTypeValue = artifact.conflictHandleType
        val conflictHandleType = if (conflictHandleTypeValue != null) {
            ConflictHandleType.fromName(conflictHandleTypeValue)
        } else {
            ConflictHandleType.FAIL
        }
        val createArtifactMetadata = client.createArtifact(
            artifact.groupId,
            artifact.id,
            artifact.version,
            artifactType.toClientArtifactType(),
            conflictHandleType.toClientConflictHandleType(),
            artifact.canonicalize,
            artifact.name,
            artifact.description,
            command.data
        )

        return ClientMetadata(createArtifactMetadata.name, createArtifactMetadata.type.toArtifactType())
    }

    /**
     * Validates that the local artifact file is compatible with its remote counterpart located on the Apicurio Schema Registry.
     *
     * @param command The command consisting of the necessary information needed to query the Apicurio Schema Registry for artifacts.
     *
     * @return the created artifact's metadata
     */
    fun isCompatible(command: ClientCommand.Compatibility) {
        val artifact = command.artifact

        client.testUpdateArtifact(artifact.groupId, artifact.id, command.data)
    }

    /**
     * Attempts to clean-up opened resources.
     */
    fun close() {
        if (this::httpClient.isInitialized) {
            httpClient.close()
        }
        client.close()
    }
}
