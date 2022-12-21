package net.croz.apicurio.core.testcontainers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class ApicurioSchemaRegistryContainerFactory {
    static GenericContainer makeAndStartContainer(String containerName = "apicurio-schema-registry", Integer httpPort = 8080) {
        String containerNameSuffix = UUID.randomUUID()
        GenericContainer apicurioSchemaRegistry = new GenericContainer(DockerImageName.parse("apicurio/apicurio-registry-mem").withTag("2.3.1.Final"))

        apicurioSchemaRegistry.with {
            withExposedPorts(httpPort)
            withCreateContainerCmdModifier { it.withName("${containerName}-${containerNameSuffix}") }
            waitingFor(Wait.forListeningPort())
            start()
        }

        return apicurioSchemaRegistry
    }
}
