plugins {
    kotlin("jvm")
}

group = "ru.stroganov.account.userauthservicek.config"
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
