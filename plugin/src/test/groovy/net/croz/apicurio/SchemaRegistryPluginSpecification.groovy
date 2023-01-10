package net.croz.apicurio

import net.croz.apicurio.task.SchemaRegistryTask
import net.croz.apicurio.task.compatibility.SchemaRegistryCompatibilityTask
import net.croz.apicurio.task.download.SchemaRegistryDownloadTask
import net.croz.apicurio.task.register.SchemaRegistryRegisterTask
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

  def "shoud apply plugin and register tasks with the extension"() {
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

  def "should create and configure download task"() {
    when:
        SchemaRegistryDownloadTask task = project.tasks.getByName(SchemaRegistryDownloadTask.TASK_NAME)

    then:
        task instanceof SchemaRegistryDownloadTask
        task.description == "Downloads artifacts from the Apicurio schema registry"
  }

  def "should create and configure register task"() {
    when:
        SchemaRegistryRegisterTask task = project.tasks.getByName(SchemaRegistryRegisterTask.TASK_NAME)

    then:
        task instanceof SchemaRegistryRegisterTask
        task.description == "Registers artifacts on the Apicurio schema registry"
  }

  def "should create and configure compatibility task"() {
    when:
        SchemaRegistryCompatibilityTask task = project.tasks.getByName(SchemaRegistryCompatibilityTask.TASK_NAME)

    then:
        task instanceof SchemaRegistryCompatibilityTask
        task.description == "Checks for compatibility issues between local artifacts and remote artifacts on the Apicurio schema registry"
  }
}
