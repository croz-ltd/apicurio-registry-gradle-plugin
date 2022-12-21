package net.croz.apicurio.task.register

import net.croz.apicurio.extension.RegisterHandler
import net.croz.apicurio.model.RegisterArtifact
import net.croz.apicurio.task.SCHEMA_REGISTRY_TASK_GROUP
import net.croz.apicurio.task.SchemaRegistryTask
import org.gradle.api.GradleScriptException
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * A task class used by the plugin to invoke an action to register local artifact(s) to the Apicurio Schema Registry from the given set of parameters.
 *
 * @see SchemaRegistryRegisterTaskAction
 */
internal abstract class SchemaRegistryRegisterTask @Inject constructor(factory: ObjectFactory) : SchemaRegistryTask() {
    internal companion object {
        internal const val TASK_NAME = "schemaRegistryRegister"

        internal fun register(project: Project, handler: RegisterHandler) =
            project.tasks.register(TASK_NAME, SchemaRegistryRegisterTask::class.java).configure {
                it.artifacts.set(handler.artifacts)
            }
    }

    init {
        group = SCHEMA_REGISTRY_TASK_GROUP
        description = "Registers artifacts on the Apicurio schema registry"
    }

    @get:Nested
    internal val artifacts: ListProperty<RegisterArtifact> =
        factory.listProperty(RegisterArtifact::class.java)

    @TaskAction
    fun register() {
        val errorCount = SchemaRegistryRegisterTaskAction(
            schemaRegistryClientService.get().client,
            project.projectDir.toPath(),
            artifacts.getOrElse(listOf())
        ).run()

        if (errorCount > 0) {
            throw GradleScriptException("$errorCount artifacts not registered, see logs for details", Throwable())
        }
    }
}