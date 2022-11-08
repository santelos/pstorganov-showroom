rootProject.name = "token-mediating-backend"

pluginManagement {
    val kotlinVersion: String by settings
    val ktorVersion: String by settings
    val ktlintGradlePluginVersion: String by settings
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintGradlePluginVersion
    }
}
