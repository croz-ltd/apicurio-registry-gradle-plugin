package net.croz.apicurio.model

import net.croz.apicurio.exception.UnknownArtifactTypeException
import net.croz.apicurio.service.client.ClientArtifactType

/**
 * Contains the artifact types available for usage with this plugin.
 * Supports the most popular serde formats that the Apicurio Schema Registry also supports.
 *
 * @property extension The associated file extension.
 */
internal enum class ArtifactType(val extension: String) {
    AVRO("avsc"),
    PROTOBUF("proto"),
    JSON("json"),
    OPENAPI("json"),
    ASYNCAPI("json"),
    GRAPHQL("graphql"),
    KCONNECT("json"),
    WSDL("wsdl"),
    XSD("xsd"),
    XML("xml");

    internal companion object {
        /**
         * Attempts to find an associated artifact type by the provided name value.
         *
         * @return the found artifact type
         * @throws UnknownArtifactTypeException if the associated artifact type is not found
         */
        fun fromName(name: String) =
            values().firstOrNull { it.name == name } ?: throw UnknownArtifactTypeException(name)
    }
}

internal fun ArtifactType.toClientArtifactType(): ClientArtifactType = when (this) {
    ArtifactType.AVRO -> ClientArtifactType.AVRO
    ArtifactType.PROTOBUF -> ClientArtifactType.PROTOBUF
    ArtifactType.JSON -> ClientArtifactType.JSON
    ArtifactType.OPENAPI -> ClientArtifactType.OPENAPI
    ArtifactType.ASYNCAPI -> ClientArtifactType.ASYNCAPI
    ArtifactType.GRAPHQL -> ClientArtifactType.GRAPHQL
    ArtifactType.KCONNECT -> ClientArtifactType.KCONNECT
    ArtifactType.WSDL -> ClientArtifactType.WSDL
    ArtifactType.XSD -> ClientArtifactType.XSD
    ArtifactType.XML -> ClientArtifactType.XML
}

internal fun ClientArtifactType.toArtifactType(): ArtifactType = when (this) {
    ClientArtifactType.AVRO -> ArtifactType.AVRO
    ClientArtifactType.PROTOBUF -> ArtifactType.PROTOBUF
    ClientArtifactType.JSON -> ArtifactType.JSON
    ClientArtifactType.OPENAPI -> ArtifactType.OPENAPI
    ClientArtifactType.ASYNCAPI -> ArtifactType.ASYNCAPI
    ClientArtifactType.GRAPHQL -> ArtifactType.GRAPHQL
    ClientArtifactType.KCONNECT -> ArtifactType.KCONNECT
    ClientArtifactType.WSDL -> ArtifactType.WSDL
    ClientArtifactType.XSD -> ArtifactType.XSD
    ClientArtifactType.XML -> ArtifactType.XML
}