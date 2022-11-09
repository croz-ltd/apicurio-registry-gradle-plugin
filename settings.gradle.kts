plugins {
    id("com.gradle.enterprise") version "3.11.4"
}

rootProject.name = "apicurio-registry-gradle-plugin"

include("sample")
includeBuild("plugin")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        tag(if (System.getenv("CI").isNullOrEmpty()) "Local" else "CI")
        tag(System.getProperty("os.name"))
    }
}