package com.github.dmuharemagic.registry.model

/**
 * A sealed class incorporating different types of artifact information acting as a domain class.
 *
 * @property id The corresponding artifact ID from the schema registry.
 * @property groupId The specific group ID from the schema registry. Default group ID is applied if omitted.
 * @property version The specific version from the schema registry. The latest version is downloaded if omitted.
 */
internal sealed class Artifact(
    open val id: String,
    open val groupId: String? = "default",
    open val version: String? = null,
) {
    /**
     * @property id The corresponding artifact ID from the schema registry.
     * @property groupId The specific group ID from the schema registry. Default group ID is applied if omitted.
     * @property version The specific version from the schema registry. The latest version is downloaded if omitted.
     */
    data class Basic(
        override val id: String,
        override val groupId: String? = "default",
        override val version: String? = null
    ) :
        Artifact(id, groupId, version)

    /**
     * @property id The corresponding artifact ID from the schema registry.
     * @property groupId The specific group ID from the schema registry. Default group ID is applied if omitted.
     * @property version The specific version from the schema registry. The latest version is downloaded if omitted.
     * @property name The name of the artifact.
     * @property description An optional description which is to be assigned to the artifact.
     * @property artifactType The artifact type which matches the local artifact file. See [ArtifactType] for supported values.
     * @property canonicalize
     * @property conflictHandleType The behaviour of what should happen when the artifact already exists on the schema registry. Defaults to register failure. See [ConflictHandleType] for more options.
     */
    data class Full(
        override val id: String,
        override val groupId: String? = "default",
        override val version: String? = null,
        val name: String,
        val description: String? = null,
        val artifactType: ArtifactType,
        val conflictHandleType: ConflictHandleType,
        val canonicalize: Boolean? = null,
    ) : Artifact(id, groupId, version)

    /**
     * Provides the fully qualified name of the artifact from the [id], [groupId] and [version] properties.
     */
    val fullName: String
        get() = if (version != null) {
            "$groupId:$id:$version"
        } else {
            "$groupId:$id"
        }
}