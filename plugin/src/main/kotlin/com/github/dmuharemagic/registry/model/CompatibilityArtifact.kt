package com.github.dmuharemagic.registry.model

import com.github.dmuharemagic.registry.task.compatibility.SchemaRegistryCompatibilityTask
import com.github.dmuharemagic.registry.task.compatibility.SchemaRegistryCompatibilityTaskAction
import org.gradle.api.tasks.Input

/**
 * A DSL structure representing the compatibility action specifics.
 *
 * @property path The directory where the local artifact file is located in.
 *
 * @see SchemaRegistryCompatibilityTask
 * @see SchemaRegistryCompatibilityTaskAction
 */
open class CompatibilityArtifact : Artifact() {
    @get:Input
    lateinit var path: String
}