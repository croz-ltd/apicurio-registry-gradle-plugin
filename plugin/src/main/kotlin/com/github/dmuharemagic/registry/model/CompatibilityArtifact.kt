package com.github.dmuharemagic.registry.model

import org.gradle.api.tasks.Input

/**
 * A DSL structure representing the compatibility action specifics.
 *
 * @property path The directory where the local artifact file is located in.
 */
open class CompatibilityArtifact : Artifact() {
    @get:Input
    lateinit var path: String
}