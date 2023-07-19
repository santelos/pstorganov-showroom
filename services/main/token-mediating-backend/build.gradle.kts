plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.oauth2.tokenmediatingbackend"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    // Ktor
    val ktorVersion: String by project
    implementation("io.ktor", "ktor-serialization-kotlinx-json", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-server-auth", ktorVersion)
    implementation("io.ktor", "ktor-server-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-server-sessions", ktorVersion)
    implementation("io.ktor", "ktor-server-call-logging", ktorVersion)
    implementation("io.ktor", "ktor-client-cio", ktorVersion)
    implementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-client-logging", ktorVersion)

    // Monitoring
    val micrometerPrometheusVersion: String by project
    implementation("io.ktor", "ktor-server-metrics-micrometer", ktorVersion)
    implementation("io.micrometer", "micrometer-registry-prometheus", micrometerPrometheusVersion)

    // Logger
    val logbackVersion: String by project
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("ru.stroganov.oauth2.tokenmediatingbackend.AppKt")
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
