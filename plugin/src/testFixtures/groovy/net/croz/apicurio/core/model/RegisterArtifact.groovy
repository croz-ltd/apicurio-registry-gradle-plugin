package net.croz.apicurio.core.model

import groovy.transform.Immutable
import io.apicurio.registry.types.ArtifactType

@Immutable
class RegisterArtifact {
    String path, groupId, artifactId, name, version
    ArtifactType type
}