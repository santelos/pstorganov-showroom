import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.spring") version "1.7.10"
}

group = "ru.stroganov.showroom.webbff"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("reflect"))
    // Coroutines
    val kotlinCoroutinesVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${kotlinCoroutinesVersion}")
    // Logging
    val kotlinLoggingVersion: String by project
    implementation("io.github.microutils:kotlin-logging:${kotlinLoggingVersion}")

    // Spring
    implementation("org.springframework.boot", "spring-boot-starter-webflux")
    implementation("org.springframework.boot", "spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot", "spring-boot-starter-data-redis")
    implementation("org.springframework.session", "spring-session-data-redis")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
