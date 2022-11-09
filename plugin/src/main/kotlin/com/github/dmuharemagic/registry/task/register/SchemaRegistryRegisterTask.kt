package com.github.dmuharemagic.registry.task.register

import com.github.dmuharemagic.registry.extension.RegisterHandler
import com.github.dmuharemagic.registry.model.Action
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
 * A task class used by the plugin to invoke an action to register local artifact(s) to the Apicurio Schema Registry from the given set of parameters.
 *
 * @see SchemaRegistryRegisterTaskAction
 */
internal abstract class SchemaRegistryRegisterTask @Inject constructor(factory: ObjectFactory) : SchemaRegistryTask() {
    internal companion object {
        internal const val TASK_NAME = "schemaRegistryRegister"

        internal fun register(project: Project, handler: RegisterHandler) =
            project.tasks.register(TASK_NAME, SchemaRegistryRegisterTask::class.java).configure {
                it.actionList.set(handler.actionList)
            }
    }

    init {
        group = SCHEMA_REGISTRY_TASK_GROUP
        description = "Registers artifacts on the Apicurio schema registry"
    }

    @get:Input
    val actionList: ListProperty<Action.Register> =
        factory.listProperty(Action.Register::class.java)

    @TaskAction
    fun register() {
        val errorCount = SchemaRegistryRegisterTaskAction(
            schemaRegistryClientService.get().client,
            project.rootDir.toPath(),
            actionList.getOrElse(listOf())
        ).run()

        if (errorCount > 0) {
            throw GradleScriptException("$errorCount artifacts not registered, see logs for details", Throwable())
        }
    }
}