plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.greben.memowave"
version = "0.0.1-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
}

val springCloudVersion = "4.3.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-config-server:${springCloudVersion}")
}

kotlin {
    jvmToolchain(21)
}