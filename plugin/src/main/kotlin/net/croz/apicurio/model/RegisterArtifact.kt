package net.croz.apicurio.model

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * A DSL structure representing the register action specifics.
 *
 * @property name The name of the artifact.
 * @property type The artifact type which matches the local artifact file. See [ArtifactType] for supported values.
 * @property path The directory where the local artifact file is located in.
 * @property description An optional description which is to be assigned to the artifact.
 * @property canonicalize Parameter that can be set to true to indicate that the server should "canonicalize" the content when searching for matching artifacts. Canonicalization is unique to each artifact type, but typically involves removing any extra whitespace and formatting the content in a consistent manner. Must be used along with the [type] query parameter.
 * @property conflictHandleType The behaviour of what should happen when the artifact already exists on the schema registry. Defaults to register failure. See [ConflictHandleType] for more options.
 */
open class RegisterArtifact : Artifact() {
    @get:Input
    lateinit var name: String

    @get:Input
    lateinit var type: String

    @get:Input
    lateinit var path: String

    @get:Input
    @get:Optional
    var description: String? = null

    @get:Input
    var canonicalize: Boolean = false

    @get:Input
    @get:Optional
    var conflictHandleType: String? = null
}