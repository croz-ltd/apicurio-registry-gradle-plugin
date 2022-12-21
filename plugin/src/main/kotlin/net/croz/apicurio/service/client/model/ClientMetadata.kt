package net.croz.apicurio.service.client.model

import net.croz.apicurio.model.ArtifactType

/**
 * Contains the artifact metadata fetched from the Apicurio Schema Registry.
 *
 * @property name The artifact name
 * @property artifactType The associated artifact type
 *
 * @see ArtifactType
 */
internal data class ClientMetadata(
    val name: String,
    val artifactType: ArtifactType
)
