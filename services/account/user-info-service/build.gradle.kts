import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.showroom.account.userinfoservice"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
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
    implementation("io.ktor", "ktor-server-sessions", ktorVersion)
    implementation("io.ktor", "ktor-server-call-logging", ktorVersion)

    // Logger
    val logbackVersion: String by project
    implementation("ch.qos.logback", "logback-classic", logbackVersion)

    // Konform
    val konformVersion: String by project
    implementation("io.konform", "konform-jvm", konformVersion)

    // DB
    val r2dbcPostgresDriver: String by project
    implementation("io.r2dbc", "r2dbc-postgresql", r2dbcPostgresDriver)
    val flywayVersion: String by project
    implementation("org.flywaydb", "flyway-core", flywayVersion)
    val jdbcPostgresDriver: String by project
    runtimeOnly("org.postgresql", "postgresql", jdbcPostgresDriver)

    // Crypt
    val bcryptVersion: String by project
    implementation("at.favre.lib", "bcrypt", bcryptVersion)

    // Test
    val junitVersion: String by project
    testImplementation("org.junit.jupiter", "junit-jupiter", junitVersion)
    val testContainersVersion: String by project
    testImplementation("org.testcontainers", "testcontainers", testContainersVersion)
    testImplementation("org.testcontainers", "junit-jupiter", testContainersVersion)
    testImplementation("org.testcontainers", "r2dbc", testContainersVersion)
    testImplementation("org.testcontainers", "postgresql", testContainersVersion)
    val mockkVersion: String by project
    testImplementation("io.mockk", "mockk", mockkVersion)

}

application {
    mainClass.set("ru.stroganov.showroom.account.userinfoservice.AppKt")
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

ktlint {
    disabledRules.set(setOf("no-wildcard-imports"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
