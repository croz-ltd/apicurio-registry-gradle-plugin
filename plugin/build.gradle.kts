import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val VERSION: String by project
version = VERSION
group = "net.croz.apicurio"

plugins {
    groovy
    `java-gradle-plugin`
    `java-test-fixtures`
    id("com.gradle.plugin-publish") version "1.1.0"
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.kotlinx.binaryCompatibilityValidator)
}

dependencies {
    implementation(libs.apicurio.client)

    testFixturesApi(libs.apicurio.client)
    testFixturesImplementation(libs.spock.testContainers)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            testType.set(TestSuiteType.UNIT_TEST)

            useSpock(libs.versions.spock.get())
        }

        val functionalTest by registering(JvmTestSuite::class) {
            testType.set(TestSuiteType.FUNCTIONAL_TEST)

            useSpock(libs.versions.spock.get())
            dependencies {
                implementation(libs.spock.testContainers)
                implementation(project.dependencies.testFixtures(project))
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("functionalTest"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<Copy>().all {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

kover {
    verify {
        onCheck.set(true)
        rule {
            isEnabled = true
            name = "Minimum code coverage"
            target = kotlinx.kover.api.VerificationTarget.ALL

            bound {
                minValue = 95
                maxValue = 100
                counter = kotlinx.kover.api.CounterType.LINE
                valueType = kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
}

tasks.withType<DokkaTask>() {
    dokkaSourceSets {
        configureEach {
            includes.from(rootProject.file("dokka/moduledoc.md").path)
        }
    }
}

gradlePlugin {
    plugins {
        create("apicurio-registry-gradle-plugin") {
            id = "net.croz.apicurio-registry-gradle-plugin"
            displayName = "Apicurio Schema Registry Gradle plugin"
            description = "A plugin to download, register and test compatibility of schemas from Apicurio Schema Registry"
            implementationClass = "net.croz.apicurio.SchemaRegistryPlugin"
        }
    }

    testSourceSets(sourceSets.getByName("functionalTest"))
}

pluginBundle {
    website = "https://github.com/croz-ltd/apicurio-registry-gradle-plugin"
    vcsUrl = "https://github.com/croz-ltd/apicurio-registry-gradle-plugin.git"
    description = "A plugin to download, register and test compatibility of schemas from Apicurio Schema Registry"
    tags = listOf("schema", "registry", "schema-registry", "apicurio", "kafka")
}

val isSnapshot = project.version.toString().endsWith("SNAPSHOT")
val isRelease = !isSnapshot

val check = tasks.named("check")

tasks.named("publishPlugins") {
    onlyIf { isRelease }

    if (isRelease) {
        dependsOn(check)
    }
}
