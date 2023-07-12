plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.account.userauthservicek.repo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":config"))

    // Logger
    val logbackVersion: String by project
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    val kotlinLoggingVersion: String by project
    implementation("io.github.microutils:kotlin-logging:${kotlinLoggingVersion}")

    // HTTP
    val ktorVersion: String by project
    implementation("io.ktor", "ktor-serialization-kotlinx-json", ktorVersion)
    implementation("io.ktor", "ktor-client-cio", ktorVersion)
    implementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-client-logging", ktorVersion)
    implementation("io.ktor", "ktor-client-auth", ktorVersion)

    // Hydra
    val shOryHydraVersion: String by project
    implementation("sh.ory.hydra", "hydra-client", shOryHydraVersion)

    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter", "junit-jupiter-params")
    val testContainersVersion: String by project
    testImplementation("org.testcontainers", "testcontainers", testContainersVersion)
    testImplementation("org.testcontainers", "junit-jupiter", testContainersVersion)
    testImplementation("org.testcontainers", "mockserver", testContainersVersion)
    val mockkVersion: String by project
    testImplementation("io.mockk", "mockk", mockkVersion)
    val mockServerVersion: String by project
    testImplementation("org.mock-server", "mockserver-client-java", mockServerVersion)
}

tasks.test {
    useJUnitPlatform()
}
