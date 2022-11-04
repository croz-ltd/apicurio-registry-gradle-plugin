package com.github.dmuharemagic.registry.util

import com.github.dmuharemagic.registry.core.model.GeneratedArtifactMetadata
import com.github.dmuharemagic.registry.model.ArtifactType

final class ArtifactMetadataGeneratingUtil {
    final static GeneratedArtifactMetadata generate() {
        def (groupId, artifactId, artifactName, artifactVersion, outputPath) = generateBaseMetadata()

        new GeneratedArtifactMetadata(groupId, artifactId, artifactName, artifactVersion, outputPath)
    }

    final static GeneratedArtifactMetadata generate(ArtifactType artifactType) {
        def (groupId, artifactId, artifactName, artifactVersion) = generateBaseMetadata()
        def outputPath = "src/main/${artifactType.name().toLowerCase()}"

        new GeneratedArtifactMetadata(groupId, artifactId, artifactName, artifactVersion, outputPath)
    }

    private static List<String> generateBaseMetadata() {
        def suffix = UUID.randomUUID().toString()
        def groupId = "artifact-group-${suffix}"
        def artifactId = "artifact-id-${suffix}"
        def artifactName = "artifact-name-${suffix}"
        def artifactVersion = "artifact-version-${suffix}"
        def outputPath = "src/main/artifact"

        [groupId, artifactId, artifactName, artifactVersion, outputPath]
    }
}
