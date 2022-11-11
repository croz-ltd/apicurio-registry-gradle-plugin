package com.github.dmuharemagic.registry.task.compatibility

import com.github.dmuharemagic.registry.model.CompatibilityArtifact
import com.github.dmuharemagic.registry.extension.CompatibilityHandler
import com.github.dmuharemagic.registry.task.SCHEMA_REGISTRY_TASK_GROUP
import com.github.dmuharemagic.registry.task.SchemaRegistryTask
import org.gradle.api.GradleScriptException
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * A task class used by the plugin to invoke an action to check the compatibility between local artifact(s) and their remote counterparts located on the Apicurio Schema Registry from the given set of parameters.
 *
 * @see SchemaRegistryCompatibilityTaskAction
 */
internal abstract class SchemaRegistryCompatibilityTask @Inject constructor(factory: ObjectFactory) :
    SchemaRegistryTask() {
    internal companion object {
        internal const val TASK_NAME = "schemaRegistryCompatibility"

        internal fun register(project: Project, handler: CompatibilityHandler) =
            project.tasks.register(
                TASK_NAME,
                SchemaRegistryCompatibilityTask::class.java
            ).configure {
                it.artifacts.set(handler.artifacts)
            }
    }

    init {
        group = SCHEMA_REGISTRY_TASK_GROUP
        description =
            "Checks for compatibility issues between local artifacts and remote artifacts on the Apicurio schema registry"
    }

    @get:Input
    val artifacts: ListProperty<CompatibilityArtifact> =
        factory.listProperty(CompatibilityArtifact::class.java)

    @TaskAction
    fun compatibility() {
        val errorCount = SchemaRegistryCompatibilityTaskAction(
            schemaRegistryClientService.get().client,
            project.projectDir.toPath(),
            artifacts.getOrElse(listOf())
        ).run()

        if (errorCount > 0) {
            throw GradleScriptException("$errorCount artifacts not compatible, see logs for details", Throwable())
        }
    }
}