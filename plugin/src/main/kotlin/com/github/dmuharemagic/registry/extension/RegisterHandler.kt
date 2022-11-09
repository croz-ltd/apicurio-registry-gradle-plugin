package com.github.dmuharemagic.registry.extension

import com.github.dmuharemagic.registry.model.Action
import com.github.dmuharemagic.registry.model.Artifact
import com.github.dmuharemagic.registry.model.ArtifactType
import com.github.dmuharemagic.registry.model.ConflictHandleType
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * A nested DSL structure which configures and lists the artifact details that are used for registering local files on the Apicurio Schema Registry.
 */
open class RegisterHandler @Inject constructor(factory: ObjectFactory) {
    internal val actionList: ListProperty<Action.Register> =
        factory.listProperty(Action.Register::class.java).apply {
            convention(listOf())
        }

    /**
     * Provide the artifact details needed to register the local artifact file on the Apicurio Schema Registry.
     * Used for an artifact with a specific group ID.
     *
     * @param groupId The specific group ID which is to be assigned to this artifact on the schema registry.
     * @param artifactId The specific artifact ID which is to be assigned to this artifact on the schema registry.
     * @param name The name of the artifact.
     * @param artifactType The artifact type which matches the local artifact file. See [ArtifactType] for supported values.
     * @param path The directory where the local artifact file is located in.
     * @param description An optional description which is to be assigned to the artifact.
     * @param version An optional version which is to be correlated with the artifact.
     * @param canonicalize
     * @param conflictHandleType The behaviour of what should happen when the artifact already exists on the schema registry. Defaults to register failure. See [ConflictHandleType] for more options.
     */
    fun artifact(
        groupId: String,
        artifactId: String,
        name: String,
        artifactType: String,
        path: String,
        description: String? = null,
        version: String? = null,
        canonicalize: Boolean? = null,
        conflictHandleType: String? = null,
    ) = actionList.add(
        Action.Register(
            Artifact.Full(
                id = artifactId,
                groupId = groupId,
                version = version,
                name = name,
                description = description,
                artifactType = ArtifactType.fromName(artifactType),
                canonicalize = canonicalize,
                conflictHandleType = if (conflictHandleType != null) {
                    ConflictHandleType.fromName(conflictHandleType)
                } else {
                    ConflictHandleType.FAIL
                }
            ),
            path
        )
    )

    /**
     * Provide the artifact details needed to register the local artifact file on the Apicurio Schema Registry.
     * Used for an artifact with a default group ID.
     *
     * @param artifactId The specific artifact ID which is to be assigned to this artifact on the schema registry.
     * @param name The name of the artifact.
     * @param artifactType The artifact type which matches the local artifact file. See [ArtifactType] for supported values.
     * @param path The directory where the local artifact file is located in.
     * @param description An optional description which is to be assigned to the artifact.
     * @param version An optional version which is to be correlated with the artifact.
     * @param canonicalize
     * @param conflictHandleType The behaviour of what should happen when the artifact already exists on the schema registry. Defaults to register failure. See [ConflictHandleType] for more options.
     */
    fun artifact(
        artifactId: String,
        name: String,
        artifactType: String,
        path: String,
        description: String? = null,
        version: String? = null,
        canonicalize: Boolean? = null,
        conflictHandleType: String? = null,
    ) = actionList.add(
        Action.Register(
            Artifact.Full(
                id = artifactId,
                version = version,
                name = name,
                description = description,
                artifactType = ArtifactType.fromName(artifactType),
                canonicalize = canonicalize,
                conflictHandleType = if (conflictHandleType != null) {
                    ConflictHandleType.fromName(conflictHandleType)
                } else {
                    ConflictHandleType.FAIL
                }
            ),
            path
        )
    )
}