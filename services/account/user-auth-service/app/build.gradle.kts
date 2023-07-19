plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.account.userauthservice.app"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":web"))

    // Ktor
    val ktorVersion: String by project
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
}

application {
    mainClass.set("ru.stroganov.account.userauthservice.app.AppKt")
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
