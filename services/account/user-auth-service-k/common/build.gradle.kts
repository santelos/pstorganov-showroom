plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "ru.stroganov.account.userauthservicek.common"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
