package net.croz.apicurio

import io.apicurio.registry.rest.v2.beans.IfExists as ClientConflictHandleType
import net.croz.apicurio.model.ConflictHandleType
import net.croz.apicurio.model.ConflictHandleTypeKt
import spock.lang.Specification

class ConflictHandleTypeKtSpecification extends Specification {
    def "should return client conflict handle type #clientConflictHandleType from conflict handle type #conflictHandleType"(ClientConflictHandleType clientConflictHandleType, ConflictHandleType conflictHandleType) {
        expect:
        ConflictHandleTypeKt.toClientConflictHandleType(conflictHandleType) == clientConflictHandleType

        where:
        conflictHandleType << ConflictHandleType.values()
        clientConflictHandleType << ClientConflictHandleType.values()
    }
}
