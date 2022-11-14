package com.github.dmuharemagic.registry.extension

import com.github.dmuharemagic.registry.model.DownloadArtifact
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * A nested DSL structure which configures and lists the artifact details that are used for downloading remote files from the Apicurio Schema Registry.
 */
open class DownloadHandler @Inject constructor(private val factory: ObjectFactory) {
    internal val artifacts: ListProperty<DownloadArtifact> =
        factory.listProperty(DownloadArtifact::class.java).apply {
            convention(listOf())
        }

    fun artifact(action: Action<DownloadArtifact>) {
        artifacts.add(factory.newInstance(DownloadArtifact::class.java).also { action.execute(it) })
    }
}