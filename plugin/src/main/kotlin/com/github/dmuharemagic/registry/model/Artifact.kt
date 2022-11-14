package com.github.dmuharemagic.registry.model

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

/**
 * A DSL structure representing the basic artifact details.
 *
/**
 * @property id The corresponding artifact ID from the schema registry.
 * @property groupId The specific group ID from the schema registry. Default group ID is applied if omitted.
 * @property version The specific version from the schema registry. The latest version is considered implicitly if omitted.
*/
 */
open class Artifact {
    @get:Input
    lateinit var id: String

    @get:Input
    @get:Optional
    var groupId: String? = null

    @get:Input
    @get:Optional
    var version: String? = null

    @get:Internal
    internal val fullName: String
        get() = if (version != null) {
            "$groupId:$id:$version"
        } else {
            "$groupId:$id"
        }
}