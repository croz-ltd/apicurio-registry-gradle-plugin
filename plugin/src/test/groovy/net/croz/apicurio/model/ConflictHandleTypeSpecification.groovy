package net.croz.apicurio.model

import net.croz.apicurio.exception.UnknownConflictHandleTypeException
import spock.lang.Specification

class ConflictHandleTypeSpecification extends Specification {
  def "should return correct conflict handle type from #conflictHandleTypeName name"(String conflictHandleTypeName) {
    expect:
        ConflictHandleType.@Companion.fromName(conflictHandleTypeName).name() == conflictHandleTypeName

    where:
        conflictHandleTypeName << ConflictHandleType.values()
  }

  def "should throw exception for non-existent conflcit handle type name"() {
    given:
        def name = "UNKNOWN"

    when:
        ConflictHandleType.@Companion.fromName(name)

    then:
        thrown(UnknownConflictHandleTypeException)
  }
}
