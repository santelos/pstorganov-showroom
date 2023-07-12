package ru.stroganov.account.userauthservicek.web.config

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import ru.stroganov.account.userauthservicek.config.AppConfig
import ru.stroganov.account.userauthservicek.service.registrationServiceImpl
import ru.stroganov.account.userauthservicek.service.remoteAuthenticationImpl
import ru.stroganov.account.userauthservicek.web.v1.oauthFlow
import ru.stroganov.account.userauthservicek.web.v1.registration

fun startServer() {
    embeddedServer(Netty) {
        serverConfig()
        authConfig()
        registration()
        oauthFlow()
    }.start(wait = true)
}

private fun Application.serverConfig() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
}
