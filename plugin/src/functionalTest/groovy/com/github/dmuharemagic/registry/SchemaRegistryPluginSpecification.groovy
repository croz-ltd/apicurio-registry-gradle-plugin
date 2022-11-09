package com.github.dmuharemagic.registry

import com.github.dmuharemagic.registry.core.specification.AbstractFunctionalSpecification

class SchemaRegistryPluginSpecification extends AbstractFunctionalSpecification {
    def setup() {
        appendPluginDefinition()
    }

    def "adds tasks"() {
        when:
        def buildResult = build("tasks")

        then:
        buildResult.output.contains(
                """
                SchemaRegistry tasks
                --------------------
                schemaRegistryCompatibility - Checks for compatibility issues between local artifacts and remote artifacts on the Apicurio schema registry
                schemaRegistryDownload - Downloads artifacts from the Apicurio schema registry
                schemaRegistryRegister - Registers artifacts on the Apicurio schema registry
                """.stripIndent()
        )
    }
}
