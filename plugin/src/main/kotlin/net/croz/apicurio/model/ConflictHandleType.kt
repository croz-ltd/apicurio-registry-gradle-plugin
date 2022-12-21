package net.croz.apicurio.model

import net.croz.apicurio.core.UnknownConflictHandleTypeException
import net.croz.apicurio.service.client.ClientConflictHandleType

/**
 * Contains the conflict handle types available for usage with this plugin which determines the behavior
 * during registering a local artifact which is the identical to its remote counterpart.
 */
internal enum class ConflictHandleType() {
    FAIL,
    UPDATE,
    RETURN,
    RETURN_OR_UPDATE;

    internal companion object {
        /**
         * Attempts to find an associated conflict handle type by the provided name value.
         *
         * @return the found conflict handle type
         * @throws UnknownConflictHandleTypeException if the associated conflict handle type is not found
         */
        fun fromName(name: String) =
            ConflictHandleType.values().firstOrNull() { it.name == name } ?: throw UnknownConflictHandleTypeException(
                name
            )
    }
}

internal fun ConflictHandleType.toClientConflictHandleType(): ClientConflictHandleType = when (this) {
    ConflictHandleType.FAIL -> ClientConflictHandleType.FAIL
    ConflictHandleType.UPDATE -> ClientConflictHandleType.UPDATE
    ConflictHandleType.RETURN -> ClientConflictHandleType.RETURN
    ConflictHandleType.RETURN_OR_UPDATE -> ClientConflictHandleType.RETURN_OR_UPDATE
}