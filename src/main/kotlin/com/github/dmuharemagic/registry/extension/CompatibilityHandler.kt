package com.github.dmuharemagic.registry.extension

import com.github.dmuharemagic.registry.model.Action
import com.github.dmuharemagic.registry.model.Artifact
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * A nested DSL structure which configures and lists the artifact details that are used for checking for compatibility issues between the local and remote artifact files.
 */
open class CompatibilityHandler @Inject constructor(factory: ObjectFactory) {
    internal val actionList: ListProperty<Action.Compatibility> =
        factory.listProperty(Action.Compatibility::class.java).apply {
            convention(listOf())
        }

    /**
     * Provide the artifact details needed to check the compatibility of the local artifact file with the one located on the Apicurio Schema Registry.
     * Used for an artifact with a specific group ID.
     *
     * @param groupId The specific group ID which is to be assigned to this artifact on the schema registry.
     * @param artifactId The specific artifact ID which is to be assigned to this artifact on the schema registry.
     * @param path The directory where the local artifact file is located in.
     */
    fun artifact(
        groupId: String,
        artifactId: String,
        path: String
    ) = actionList.add(
        Action.Compatibility(
            Artifact.Basic(id = artifactId, groupId = groupId),
            path
        )
    )

    /**
     * Provide the artifact details needed to check the compatibility of the local artifact file with the one located on the Apicurio Schema Registry.
     * Used for an artifact with a default group ID.
     *
     * @param artifactId The specific artifact ID which is to be assigned to this artifact on the schema registry.
     * @param path The directory where the local artifact file is located in.
     */
    fun artifact(
        artifactId: String,
        path: String
    ) = actionList.add(
        Action.Compatibility(
            Artifact.Basic(id = artifactId),
            path = path
        )
    )
}