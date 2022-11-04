package com.github.dmuharemagic.registry.util

import com.github.dmuharemagic.registry.core.model.RegisterArtifact
import com.github.dmuharemagic.registry.core.model.RetrieveArtifactMetadata
import io.apicurio.registry.rest.client.RegistryClientFactory
import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.rest.v2.beans.IfExists
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class SchemaRegistryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaRegistryUtil.class);

    private SchemaRegistryUtil() {
        throw new UnsupportedOperationException("Utility class")
    }

    static void registerArtifact(String containerBaseUrl, RegisterArtifact artifact) {
        def resource = IOUtil.getResource(artifact.path)

        def client = RegistryClientFactory.create(containerBaseUrl)
        client.createArtifact(artifact.groupId, artifact.artifactId, artifact.version, artifact.type, IfExists.FAIL, null, artifact.name, null, resource.newInputStream())
        client.close()
    }

    static ArtifactMetaData retrieveArtifactMetadata(String containerBaseUrl, RetrieveArtifactMetadata artifact) {
        def client = RegistryClientFactory.create(containerBaseUrl)
        def data = client.getArtifactMetaData(artifact.groupId, artifact.artifactId)
        client.close()

        data
    }
}
