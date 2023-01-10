package net.croz.apicurio.model

import io.apicurio.registry.types.ArtifactType as ClientArtifactType
import spock.lang.Specification

class ArtifactTypeKtSpecification extends Specification {
  def "should return artifact type #artifactType from client artifact type #clientArtifactType"(ArtifactType artifactType, ClientArtifactType clientArtifactType) {
    expect:
        ArtifactTypeKt.toArtifactType(clientArtifactType) == artifactType

    where:
        clientArtifactType << ClientArtifactType.values()
        artifactType << ArtifactType.values()
  }

  def "should return client artifact type #clientArtifactType from artifact type #artifactType"(ClientArtifactType clientArtifactType, ArtifactType artifactType) {
    expect:
        ArtifactTypeKt.toClientArtifactType(artifactType) == clientArtifactType

    where:
        artifactType << ArtifactType.values()
        clientArtifactType << ClientArtifactType.values()
  }
}
