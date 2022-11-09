plugins {
    id("apicurio-registry-gradle-plugin") version "0.1-SNAPSHOT"
}

schemaRegistry {
    config {
        url("http://localhost:8080")
    }
    register {
        artifact(
            groupId = "test",
            artifactId = "person",
            name = "Person",
            type = "AVRO",
            path = "example/support/schema/Person.avsc"
        )
    }
}