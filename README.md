[![CI](https://github.com/dmuharemagic/apicurio-registry-gradle-plugin/actions/workflows/push.yml/badge.svg?branch=main)](https://github.com/dmuharemagic/apicurio-registry-gradle-plugin/actions/workflows/push.yml) [![codecov](https://codecov.io/github/dmuharemagic/apicurio-registry-gradle-plugin/branch/main/graph/badge.svg?token=DMXAKK77GW)](https://codecov.io/github/dmuharemagic/apicurio-registry-gradle-plugin)

# Apicurio Schema Registry Gradle plugin

The aim of this plugin is to adapt
the [Apicurio Schema Registry Maven plugin](https://www.apicur.io/registry/docs/apicurio-registry/1.3.3.Final/getting-started/assembly-managing-registry-artifacts-maven.html)
for Gradle builds.

## Usage

To apply this plugin to your project, add the following line to your root `build.gradle`:

```groovy
plugins {
    id 'apicurio-registry-gradle-plugin' version "<<latest_version>>"
}
```

or `build.gradle.kts`:

```kotlin
plugins {
    id("apicurio-registry-gradle-plugin") version "<<latest_version>>"
}
```

For detailed usage instructions, please consult [the wiki]().

## Tasks

After a successful installation of the plugin from one of the aforementioned repositories, three tasks are registered
under the `schemaRegistry` group:

* `schemaRegistryDownload` - Downloads remote artifacts from the Apicurio schema registry
* `schemaRegistryRegister` - Registers local artifacts on the Apicurio schema registry
* `schemaRegistryCompatibility` - Checks for compatibility issues between local artifacts and their remote counterparts
  on the Apicurio schema registry
