package net.croz.apicurio.extension

import net.croz.apicurio.model.Authentication
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * A nested DSL structure which configures the connection information (URL, authentication) need to connect to the Apicurio Schema Registry.
 */
open class ConfigurationHandler @Inject constructor(factory: ObjectFactory) {
    internal val url: Property<String> = factory.property(String::class.java)
    internal val authentication: Property<Authentication> =
        factory.property(Authentication::class.java)
            .convention(Authentication.None)

    /**
     * Provide the schema registry URL which is used internally to invoke operations on the Apicurio Schema Registry.
     */
    fun url(path: String) {
        url.set(path)
        url.disallowChanges()
    }

    /**
     * Provide the authentication details in case the Apicurio Schema Registry uses Basic Authentication.
     *
     * @param username The basic authentication username.
     * @param password The basic authentication password.
     */
    fun auth(username: String, password: String) {
        authentication.set(Authentication.Basic(username, password))
        authentication.disallowChanges()
    }

    /**
     * Provide the authentication details in case the Apicurio Schema Registry uses OAuth.
     *
     * @param authServerUrl The URL of the authentication server used for the Apicurio Schema Registry.
     * @param clientId The client ID for the authentication server.
     * @param clientSecret The client secret passphrase for the authentication server.
     */
    fun auth(authServerUrl: String, clientId: String, clientSecret: String) {
        authentication.set(
            Authentication.OAuth(
                authServerUrl,
                clientId,
                clientSecret
            )
        )
        authentication.disallowChanges()
    }
}
