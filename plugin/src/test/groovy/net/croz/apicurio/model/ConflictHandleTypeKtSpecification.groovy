package net.croz.apicurio.model

import io.apicurio.registry.rest.v2.beans.IfExists as ClientConflictHandleType
import spock.lang.Specification

class ConflictHandleTypeKtSpecification extends Specification {
  def "should return client conflict handle type #clientConflictHandleType from conflict handle type #conflictHandleType"(
      ClientConflictHandleType clientConflictHandleType, ConflictHandleType conflictHandleType)
  {
    expect:
        ConflictHandleTypeKt.toClientConflictHandleType(conflictHandleType) == clientConflictHandleType

    where:
        conflictHandleType << ConflictHandleType.values()
        clientConflictHandleType << ClientConflictHandleType.values()
  }
}
