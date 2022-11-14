package com.github.dmuharemagic.registry

import com.github.dmuharemagic.registry.task.SchemaRegistryTask
import com.github.dmuharemagic.registry.task.compatibility.SchemaRegistryCompatibilityTask
import com.github.dmuharemagic.registry.task.download.SchemaRegistryDownloadTask
import com.github.dmuharemagic.registry.task.register.SchemaRegistryRegisterTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class SchemaRegistryPluginSpecification extends Specification {
    @Shared
    private Project project = ProjectBuilder.builder().build()

    def setupSpec() {
        project.apply(plugin: SchemaRegistryPlugin)
    }

    def "plugin should be applied and tasks should be registered and extension should be created"() {
        expect:
        project.plugins.hasPlugin(SchemaRegistryPlugin)
        project.tasks.withType(SchemaRegistryTask)
                .names
                .containsAll(
                        SchemaRegistryDownloadTask.TASK_NAME,
                        SchemaRegistryRegisterTask.TASK_NAME,
                        SchemaRegistryCompatibilityTask.TASK_NAME
                )
        project.extensions.getByName(SchemaRegistryExtension.EXTENSION_NAME) instanceof SchemaRegistryExtension
    }

    def "creates and configures download task"() {
        expect:
        SchemaRegistryExtension extension = project.extensions.getByName(SchemaRegistryExtension.EXTENSION_NAME)
        SchemaRegistryDownloadTask task = project.tasks.getByName(SchemaRegistryDownloadTask.TASK_NAME)
        task instanceof SchemaRegistryDownloadTask
        task.description == "Downloads artifacts from the Apicurio schema registry"
    }

    def "creates and configures register task"() {
        expect:
        SchemaRegistryExtension extension = project.extensions.getByName(SchemaRegistryExtension.EXTENSION_NAME)
        SchemaRegistryRegisterTask task = project.tasks.getByName(SchemaRegistryRegisterTask.TASK_NAME)
        task instanceof SchemaRegistryRegisterTask
        task.description == "Registers artifacts on the Apicurio schema registry"
    }

    def "creates and configures compatibility task"() {
        expect:
        SchemaRegistryExtension extension = project.extensions.getByName(SchemaRegistryExtension.EXTENSION_NAME)
        SchemaRegistryCompatibilityTask task = project.tasks.getByName(SchemaRegistryCompatibilityTask.TASK_NAME)
        task instanceof SchemaRegistryCompatibilityTask
        task.description == "Checks for compatibility issues between local artifacts and remote artifacts on the Apicurio schema registry"
    }
}