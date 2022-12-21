package net.croz.apicurio.extension

import net.croz.apicurio.model.RegisterArtifact
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * A nested DSL structure which configures and lists the artifact details that are used for registering local files on the Apicurio Schema Registry.
 */
open class RegisterHandler @Inject constructor(private val factory: ObjectFactory) {
    internal val artifacts: ListProperty<RegisterArtifact> =
        factory.listProperty(RegisterArtifact::class.java).apply {
            convention(listOf())
        }

    fun artifact(action: Action<RegisterArtifact>) {
        artifacts.add(factory.newInstance(RegisterArtifact::class.java).also { action.execute(it) })
    }
}