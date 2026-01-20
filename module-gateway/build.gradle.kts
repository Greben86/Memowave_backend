plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
//    id("org.springframework.cloud") version "2025.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.greben.memowave"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.3.0")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux:4.3.0")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:3.3.0")
    implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux:4.3.0")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation(kotlin("test"))
}