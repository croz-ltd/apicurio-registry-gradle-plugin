package net.croz.apicurio.extension

import net.croz.apicurio.model.CompatibilityArtifact
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * A nested DSL structure which configures and lists the artifact details that are used for checking for compatibility issues between the local and remote artifact files.
 */
open class CompatibilityHandler @Inject constructor(private val factory: ObjectFactory) {
    internal val artifacts: ListProperty<CompatibilityArtifact> =
        factory.listProperty(CompatibilityArtifact::class.java).apply {
            convention(listOf())
        }

    fun artifact(action: Action<CompatibilityArtifact>) {
        artifacts.add(factory.newInstance(CompatibilityArtifact::class.java).also { action.execute(it) })
    }
}