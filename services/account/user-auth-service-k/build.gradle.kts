plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.account.userauthservicek"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":web"))
}

application {
    mainClass.set("ru.stroganov.showroom.account.userinfoservicek.AppKt")
}

kotlin {
    jvmToolchain(17)
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
