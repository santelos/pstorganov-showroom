package ru.stroganov.showroom.account.userinfoservice

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import ru.stroganov.showroom.account.userinfoservice.repo.FlywayRepo
import ru.stroganov.showroom.account.userinfoservice.repo.FlywayRepoObject
import ru.stroganov.showroom.account.userinfoservice.routing.v1.userModule

fun main() {
    embeddedServer(Netty) {
        onStartup()
        serverConfig()
        userModule()
    }.start(wait = true)
}

private fun Application.serverConfig() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
}

private fun Application.onStartup(flywayRepo: FlywayRepo = FlywayRepoObject) {
    flywayRepo.applyMigration()
}
