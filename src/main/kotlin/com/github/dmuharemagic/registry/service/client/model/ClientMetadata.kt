package com.github.dmuharemagic.registry.service.client.model

import com.github.dmuharemagic.registry.model.ArtifactType

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
