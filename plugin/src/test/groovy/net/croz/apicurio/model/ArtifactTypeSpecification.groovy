package net.croz.apicurio.model

import net.croz.apicurio.exception.UnknownArtifactTypeException
import spock.lang.Specification

class ArtifactTypeSpecification extends Specification {
  def "should return correct artifact type from #artifactTypeName name"(String artifactTypeName) {
    expect:
        ArtifactType.@Companion.fromName(artifactTypeName).name() == artifactTypeName

    where:
        artifactTypeName << ArtifactType.values()
  }

  def "should throw exception for non-existent artifact type name"() {
    given:
        def name = "UNKNOWN"

    when:
        ArtifactType.@Companion.fromName(name)

    then:
        thrown(UnknownArtifactTypeException)
  }
}
