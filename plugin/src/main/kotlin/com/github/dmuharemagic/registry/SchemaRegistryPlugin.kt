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

        applyService(schemaRegistry)
        applyTasks(schemaRegistry)
    }

    private fun Project.applyService(
        schemaRegistry: SchemaRegistryExtension
    ) {
        val schemaRegistryClientServiceProvider = SchemaRegistryClientService.register(this, schemaRegistry)

        tasks.withType(SchemaRegistryTask::class.java).configureEach { task ->
            task.schemaRegistryClientService.set(schemaRegistryClientServiceProvider)
            task.usesService(schemaRegistryClientServiceProvider)
        }
    }

    private fun Project.applyTasks(
        schemaRegistry: SchemaRegistryExtension
    ) {
        SchemaRegistryDownloadTask.register(this, schemaRegistry.download)
        SchemaRegistryRegisterTask.register(this, schemaRegistry.register)
        SchemaRegistryCompatibilityTask.register(this, schemaRegistry.compatibility)
    }
}