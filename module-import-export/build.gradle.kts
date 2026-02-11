plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
//    kotlin("kapt") version "1.9.22"
}

group = "dev.greben.memowave"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

val jsonwebtokenVersion = "0.12.3"
val openApiVersion = "2.8.14"
val mapstructVersion = "1.6.3"
val mockitoVersion = "5.14.0"
val commonsLangVersion = "3.17.0"
val loggingVersion = "7.0.3"
val minioVersion = "8.5.17"

dependencies {
    implementation(project(":module-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation(group = "io.jsonwebtoken", name = "jjwt-api", version = jsonwebtokenVersion)
//    implementation(group = "io.jsonwebtoken", name = "jjwt-impl", version = jsonwebtokenVersion)
//    implementation(group = "io.jsonwebtoken", name = "jjwt-jackson", version = jsonwebtokenVersion)
    implementation(group = "org.springdoc", name = "springdoc-openapi-starter-webmvc-ui", version = openApiVersion)
    implementation(group = "org.mapstruct", name = "mapstruct", version = mapstructVersion)
//    kapt(group = "org.mapstruct", name = "mapstruct-processor", version = mapstructVersion)
    implementation("org.apache.commons:commons-lang3")
    implementation(group = "io.github.oshai", name = "kotlin-logging-jvm", version = loggingVersion)
    implementation("io.minio:minio:$minioVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}