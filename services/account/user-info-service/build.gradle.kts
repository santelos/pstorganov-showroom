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
    implementation("io.ktor", "ktor-client-cio", ktorVersion)
    implementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-client-logging", ktorVersion)

    // Logger
    val logbackVersion: String by project
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    val kotlinLoggingVersion: String by project
    implementation("io.github.microutils:kotlin-logging:${kotlinLoggingVersion}")

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
    val kotestVersion: String by project
    testImplementation("io.kotest", "kotest-runner-junit5", kotestVersion)
    testImplementation("io.kotest", "kotest-assertions-core", kotestVersion)
    testImplementation("io.kotest", "kotest-assertions-core", kotestVersion)
    val kotestKtorVersion: String by project
    testImplementation("io.kotest.extensions", "kotest-assertions-ktor", kotestKtorVersion)
    val kotestTestcontainersVersion: String by project
    testImplementation("io.kotest.extensions", "kotest-extensions-testcontainers", kotestTestcontainersVersion)
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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
