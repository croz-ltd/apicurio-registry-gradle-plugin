package net.croz.apicurio.task

import net.croz.apicurio.service.SchemaRegistryClientService
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

/**
 * The task group that is assigned to all the tasks that this plugin consists of.
 */
internal const val SCHEMA_REGISTRY_TASK_GROUP = "schemaRegistry"

/**
 * The abstract schema registry task which applies common behaviour to all the tasks that this plugin consists of.
 *
 * @property schemaRegistryClientService The shared build service used for network operations with the Apicurio Schema Registry.
 *
 * @see SchemaRegistryClientService
 * @see SchemaRegistryTaskAction
 */
internal abstract class SchemaRegistryTask : DefaultTask() {
    @get:Internal
    abstract val schemaRegistryClientService: Property<SchemaRegistryClientService>
}