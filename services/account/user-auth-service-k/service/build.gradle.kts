plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.account.userauthservicek.service"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":config"))
    implementation(project(":repo"))

    val kotlinxVersion: String by project
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", kotlinxVersion)

    // Logger
    val logbackVersion: String by project
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    val kotlinLoggingVersion: String by project
    implementation("io.github.microutils:kotlin-logging:${kotlinLoggingVersion}")

    // Test
    testImplementation(kotlin("test"))
    val mockkVersion: String by project
    testImplementation("io.mockk", "mockk", mockkVersion)
}

tasks.test {
    useJUnitPlatform()
}
