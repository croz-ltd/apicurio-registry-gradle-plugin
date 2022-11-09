import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val VERSION: String by project
version = VERSION
group = "com.github.dmuharemagic"

plugins {
    id("groovy")
    id("java-gradle-plugin")
    id("java-test-fixtures")
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.kotlinx.binaryCompatibilityValidator)
}

// Dependencies
dependencies {
    implementation(libs.apicurioRegistryClient)

    testFixturesApi(libs.apicurioRegistryClient)
    testFixturesImplementation(libs.spockTestContainers)
}

// Compile
java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

// Test
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
                implementation(libs.bundles.functionalTestDependencies)
                implementation(project.dependencies.testFixtures(project))
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)

                        beforeTest(closureOf<TestDescriptor> {
                            logger.lifecycle("Running test: $this")
                        })
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

// Documentation
tasks.withType<DokkaTask>() {
    dokkaSourceSets {
        configureEach {
            includes.from(rootProject.file("dokka/moduledoc.md").path)
        }
    }
}

// Plugin
gradlePlugin {
    plugins {
        create("apicurio-registry-gradle-plugin") {
            id = "apicurio-registry-gradle-plugin"
            implementationClass = "com.github.dmuharemagic.registry.SchemaRegistryPlugin"
        }
    }

    testSourceSets(sourceSets.getByName("functionalTest"))
}

// Publish
// Local Maven Publish - to be deleted
publishing {
    repositories {
        mavenLocal()
    }
}

// TODO: uncomment this and setup publishing
//val isSnapshot = project.version.toString().endsWith("SNAPSHOT")
//val isRelease = !isSnapshot
//
//val check = tasks.named("check")
//val integrationTest = tasks.named("integrationTest")
//
//val publishToMavenCentral = tasks.named("publishToMavenCentral") {
//    if (isRelease) {
//        dependsOn(check, integrationTest)
//    }
//}
//
//val publishToPluginPortal = tasks.named("publishPlugins") {
//    onlyIf { isRelease }
//    shouldRunAfter(publishToMavenCentral)
//
//    if (isRelease) {
//        dependsOn(check, integrationTest)
//    }
//}
//
//tasks.register("publishAll") {
//    dependsOn(publishToMavenCentral, publishToPluginPortal)
//
//    group = "publishing"
//    description = "Publishes the plugin to Maven Central and the Gradle Plugin Portal"
//}