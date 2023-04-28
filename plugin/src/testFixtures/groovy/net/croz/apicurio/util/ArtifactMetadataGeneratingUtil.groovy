package net.croz.apicurio.util

import net.croz.apicurio.core.model.GeneratedArtifactMetadata

final class ArtifactMetadataGeneratingUtil {
  private ArtifactMetadataGeneratingUtil() {
    throw new UnsupportedOperationException("Utility class")
  }

  final static GeneratedArtifactMetadata generate() {
    def (groupId, artifactId, artifactName, artifactVersion, outputPath) = generateBaseMetadata()

    new GeneratedArtifactMetadata(groupId, artifactId, artifactName, artifactVersion, outputPath)
  }

  final static GeneratedArtifactMetadata generate(String artifactType) {
    def (groupId, artifactId, artifactName, artifactVersion) = generateBaseMetadata()
    def outputPath = "src/main/${artifactType.toLowerCase()}"

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
