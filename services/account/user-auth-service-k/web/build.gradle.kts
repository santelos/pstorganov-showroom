plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.account.userauthservicek.web"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":config"))
    implementation(project(":service"))

    // Kotlin
    implementation(kotlin("reflect"))
    val kotlinxVersion: String by project
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-reactive", kotlinxVersion)

    // Ktor
    val ktorVersion: String by project
    implementation("io.ktor", "ktor-serialization-kotlinx-json", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-server-auth", ktorVersion)
    implementation("io.ktor", "ktor-server-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-server-call-logging", ktorVersion)
    implementation("io.ktor", "ktor-server-status-pages", ktorVersion)
    implementation("io.ktor", "ktor-client-cio", ktorVersion)
    implementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)

    // Logger
    val logbackVersion: String by project
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    val kotlinLoggingVersion: String by project
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("io.ktor", "ktor-server-test-host", ktorVersion)
    val mockkVersion: String by project
    testImplementation("io.mockk", "mockk", mockkVersion)
}

tasks.test {
    useJUnitPlatform()
}
