package com.github.dmuharemagic.registry.model

import com.github.dmuharemagic.registry.service.SchemaRegistryClientService
import com.github.dmuharemagic.registry.service.client.SchemaRegistryClient
import java.io.Serializable

/**
 * A sealed class depicting different types of authentication available to provide authentication data for the Apicurio Schema Registry.
 *
 * @see SchemaRegistryClientService
 * @see SchemaRegistryClient
 */
internal sealed class Authentication : Serializable {
    /**
     * No authentication is applied.
     */
    object None : Authentication()

    /**
     * Provides authentication details for basic authentication type.
     *
     * @param username The basic authentication username.
     * @param password The basic authentication password.
     */
    data class Basic(val username: String, val password: String) : Authentication()

    /**
     * Provides authentication details for OAuth authentication type.
     *
     * @property authServerUrl The URL of the authentication server used for the Apicurio Schema Registry.
     * @property clientId The client ID for the authentication server.
     * @property clientSecret The client secret passphrase for the authentication server.
     */
    data class OAuth(val authServerUrl: String, val clientId: String, val clientSecret: String) :
        Authentication()
}