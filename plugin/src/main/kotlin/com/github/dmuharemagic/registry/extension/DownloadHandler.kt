package com.github.dmuharemagic.registry.extension

import com.github.dmuharemagic.registry.model.Action
import com.github.dmuharemagic.registry.model.Artifact
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * A nested DSL structure which configures and lists the artifact details that are used for downloading remote files from the Apicurio Schema Registry.
 */
open class DownloadHandler @Inject constructor(factory: ObjectFactory) {
    internal val actionList: ListProperty<Action.Download> =
        factory.listProperty(Action.Download::class.java).apply {
            convention(listOf())
        }

    /**
     * Provide the artifact details needed to download the remote file from the Apicurio Schema Registry.
     *
     * @param groupId The specific group ID from the schema registry. Default group ID is applied if omitted.
     * @param artifactId The corresponding artifact ID from the schema registry.
     * @param version The specific version from the schema registry. The latest version is considered if omitted.
     * @param outputPath The directory to where the remote file should be downloaded to.
     * @param outputFileName Customizable file name of the remote file to be downloaded. The artifact name fetched from the schema registry is applied if omitted.
     */
    fun artifact(
        groupId: String? = null,
        artifactId: String,
        version: String? = null,
        outputPath: String,
        outputFileName: String? = null
    ) =
        actionList.add(
            Action.Download(
                Artifact.Basic(id = artifactId, groupId = groupId, version = version),
                outputPath,
                outputFileName
            )
        )
}