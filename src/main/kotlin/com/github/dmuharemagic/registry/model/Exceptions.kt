package com.github.dmuharemagic.registry.core

import com.github.dmuharemagic.registry.model.ArtifactType
import com.github.dmuharemagic.registry.model.ConflictHandleType

/**
 * An exception representing a state where an artifact type name cannot be associated with from the given string value.
 * Used by [ArtifactType] internally.
 *
 * @see ArtifactType
 */
internal class UnknownArtifactTypeException(type: String) :
    Exception(
        "Cannot derive artifact type from name [${type}]. Possible values include: [${
            ArtifactType.values().joinToString(", ")
        }]."
    )

/**
 * An exception representing a state where a conflict handle type name cannot be associated with from the given string value.
 * Used by [ConflictHandleType] internally.
 *
 * @see ConflictHandleType
 */
internal class UnknownConflictHandleTypeException(type: String) :
    Exception(
        "Cannot derive conflict handle type from name [${type}]. Possible values include: [${
            ConflictHandleType.values().joinToString(", ")
        }]."
    )