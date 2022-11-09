package com.github.dmuharemagic.registry.task

import com.github.dmuharemagic.registry.task.compatibility.SchemaRegistryCompatibilityTaskAction
import com.github.dmuharemagic.registry.task.download.SchemaRegistryDownloadTaskAction
import com.github.dmuharemagic.registry.task.register.SchemaRegistryRegisterTaskAction

/**
 * An interface defining common behavioral structure for all the task actions which are invoked by the individual tasks.
 *
 * @see SchemaRegistryDownloadTaskAction
 * @see SchemaRegistryRegisterTaskAction
 * @see SchemaRegistryCompatibilityTaskAction
 */
internal interface SchemaRegistryTaskAction {
    /**
     * Executes the underlying task actions and returns the occured error count.
     *
     * @return error count, if any
     */
    fun run(): Int
}