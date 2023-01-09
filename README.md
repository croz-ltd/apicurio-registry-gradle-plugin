[![CI](https://github.com/croz-ltd/apicurio-registry-gradle-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/croz-ltd/apicurio-registry-gradle-plugin/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/croz-ltd/apicurio-registry-gradle-plugin/branch/main/graph/badge.svg?token=t54Rqfd1UO)](https://codecov.io/gh/croz-ltd/apicurio-registry-gradle-plugin)
[![Dokka](https://img.shields.io/badge/API%20Documentation-Dokka-important.svg)](https://croz-ltd.github.io/apicurio-registry-gradle-plugin/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)

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

## License

    Copyright (c) 2023 CROZ Ltd.
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
