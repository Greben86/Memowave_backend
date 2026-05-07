plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("kapt") version "1.9.22"
}

group = "dev.greben.memowave"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

val springCloudVersion = "4.3.0"
val jsonwebtokenVersion = "0.12.3"
val openApiVersion = "2.8.14"
val mapstructVersion = "1.6.3"
val mockitoVersion = "5.14.0"
val commonsLangVersion = "3.17.0"
val loggingVersion = "7.0.3"
val opensmppVersion = "3.0.2"

dependencies {
    implementation(project(":module-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.opensmpp:opensmpp-core:$opensmppVersion")
    implementation("org.springframework.cloud:spring-cloud-starter-config:${springCloudVersion}")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:${springCloudVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(group = "io.jsonwebtoken", name = "jjwt-api", version = jsonwebtokenVersion)
    implementation(group = "io.jsonwebtoken", name = "jjwt-impl", version = jsonwebtokenVersion)
    implementation(group = "io.jsonwebtoken", name = "jjwt-jackson", version = jsonwebtokenVersion)
    implementation(group = "org.springdoc", name = "springdoc-openapi-starter-webmvc-ui", version = openApiVersion)
    implementation(group = "org.mapstruct", name = "mapstruct", version = mapstructVersion)
    kapt(group = "org.mapstruct", name = "mapstruct-processor", version = mapstructVersion)
    implementation("org.apache.commons:commons-lang3")
    runtimeOnly("org.postgresql:postgresql")
    implementation(group = "io.github.oshai", name = "kotlin-logging-jvm", version = loggingVersion)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}