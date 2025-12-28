plugins {
    kotlin("jvm") version "1.9.25"
}

group = "dev.greben.memowave"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val swaggerVersion = "2.2.36"
val jakartaValidationVersion = "3.0.2"

dependencies {
    implementation(group = "jakarta.validation", name = "jakarta.validation-api", version = jakartaValidationVersion)
    implementation(group = "io.swagger.core.v3", name = "swagger-annotations-jakarta", version = swaggerVersion)
    implementation(group = "io.swagger.core.v3", name = "swagger-annotations", version = swaggerVersion)
}

kotlin {
    jvmToolchain(21)
}