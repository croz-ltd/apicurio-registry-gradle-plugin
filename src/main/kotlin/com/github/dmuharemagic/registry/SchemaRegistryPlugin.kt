package com.github.dmuharemagic.registry

import com.github.dmuharemagic.registry.SchemaRegistryExtension.Companion.schemaRegistry
import com.github.dmuharemagic.registry.service.SchemaRegistryClientService
import com.github.dmuharemagic.registry.task.SchemaRegistryTask
import com.github.dmuharemagic.registry.task.compatibility.SchemaRegistryCompatibilityTask
import com.github.dmuharemagic.registry.task.download.SchemaRegistryDownloadTask
import com.github.dmuharemagic.registry.task.register.SchemaRegistryRegisterTask
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * The entrypoint for this plugin.
 *
 * Defines the DSL structure and applies the necessary shared build service, along with the implemented tasks.
 *
 * @see SchemaRegistryExtension
 * @see SchemaRegistryDownloadTask
 * @see SchemaRegistryRegisterTask
 * @see SchemaRegistryCompatibilityTask
 */
class SchemaRegistryPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.run {
        val schemaRegistry = schemaRegistry()

        applyService(project, schemaRegistry)
        applyTasks(project, schemaRegistry)
    }

    private fun Project.applyService(
        project: Project,
        schemaRegistry: SchemaRegistryExtension
    ) {
        val schemaRegistryClientServiceProvider = SchemaRegistryClientService.register(project, schemaRegistry)

        tasks.withType(SchemaRegistryTask::class.java).configureEach { task ->
            task.schemaRegistryClientService.set(schemaRegistryClientServiceProvider)
            task.usesService(schemaRegistryClientServiceProvider)
        }
    }

    private fun applyTasks(
        project: Project,
        schemaRegistry: SchemaRegistryExtension
    ) {
        SchemaRegistryDownloadTask.register(project, schemaRegistry.download)
        SchemaRegistryRegisterTask.register(project, schemaRegistry.register)
        SchemaRegistryCompatibilityTask.register(project, schemaRegistry.compatibility)
    }
}