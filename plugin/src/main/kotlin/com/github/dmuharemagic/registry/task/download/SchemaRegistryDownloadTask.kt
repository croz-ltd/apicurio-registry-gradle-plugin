package com.github.dmuharemagic.registry.task.download

import com.github.dmuharemagic.registry.extension.DownloadHandler
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
 * A task class used by the plugin to invoke an action to download remote artifact(s) located on the Apicurio Schema Registry from the given set of parameters.
 *
 * @see SchemaRegistryDownloadTaskAction
 */
internal abstract class SchemaRegistryDownloadTask @Inject constructor(factory: ObjectFactory) : SchemaRegistryTask() {
    internal companion object {
        internal const val TASK_NAME = "schemaRegistryDownload"

        internal fun register(project: Project, handler: DownloadHandler) =
            project.tasks.register(TASK_NAME, SchemaRegistryDownloadTask::class.java).configure {
                it.actionList.set(handler.actionList)
            }
    }

    init {
        group = SCHEMA_REGISTRY_TASK_GROUP
        description = "Downloads artifacts from the Apicurio schema registry"
    }

    @get:Input
    val actionList: ListProperty<Action.Download> =
        factory.listProperty(Action.Download::class.java)

    @TaskAction
    fun download() {
        val errorCount = SchemaRegistryDownloadTaskAction(
            schemaRegistryClientService.get().client,
            project.rootDir.toPath(),
            actionList.getOrElse(listOf())
        ).run()

        if (errorCount > 0) {
            throw GradleScriptException("$errorCount artifacts not downloaded, see logs for details", Throwable())
        }
    }
}