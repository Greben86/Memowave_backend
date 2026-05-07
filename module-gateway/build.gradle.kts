plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.greben.memowave"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

val springCloudVersion = "4.3.0"
val resilience4jVersion = "3.3.0"
val openApiVersion = "2.8.14"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:${springCloudVersion}")
    implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux:${springCloudVersion}")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux:${springCloudVersion}")
    implementation("org.springframework.cloud:spring-cloud-starter-config:${springCloudVersion}")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:${springCloudVersion}")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:${resilience4jVersion}")

    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:$openApiVersion")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}