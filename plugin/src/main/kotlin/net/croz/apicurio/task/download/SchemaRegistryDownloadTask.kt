package net.croz.apicurio.task.download

import net.croz.apicurio.extension.DownloadHandler
import net.croz.apicurio.model.DownloadArtifact
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
 * A task class used by the plugin to invoke an action to download remote artifact(s) located on the Apicurio Schema Registry from the given set of parameters.
 *
 * @see SchemaRegistryDownloadTaskAction
 */
internal abstract class SchemaRegistryDownloadTask @Inject constructor(factory: ObjectFactory) : SchemaRegistryTask() {
    internal companion object {
        internal const val TASK_NAME = "schemaRegistryDownload"

        internal fun register(project: Project, handler: DownloadHandler) =
            project.tasks.register(TASK_NAME, SchemaRegistryDownloadTask::class.java).configure {
                it.artifacts.set(handler.artifacts)
            }
    }

    init {
        group = SCHEMA_REGISTRY_TASK_GROUP
        description = "Downloads artifacts from the Apicurio schema registry"
    }

    @get:Nested
    internal val artifacts: ListProperty<DownloadArtifact> =
        factory.listProperty(DownloadArtifact::class.java)

    @TaskAction
    fun download() {
        val errorCount = SchemaRegistryDownloadTaskAction(
            schemaRegistryClientService.get().client,
            project.projectDir.toPath(),
            artifacts.getOrElse(listOf())
        ).run()

        if (errorCount > 0) {
            throw GradleScriptException("$errorCount artifacts not downloaded, see logs for details", Throwable())
        }
    }
}