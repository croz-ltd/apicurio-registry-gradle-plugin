package net.croz.apicurio.exception

import net.croz.apicurio.model.ConflictHandleType

/**
 * An exception representing a state where a conflict handle type name cannot be associated with from the given string value.
 * Used by [ConflictHandleType] internally.
 *
 * @see ConflictHandleType
 */
internal class UnknownConflictHandleTypeException(type: String) :
    Exception(
        "Cannot derive conflict handle type from name [${type}]. Possible values include: [${
            ConflictHandleType.values().joinToString()
        }]."
    )
