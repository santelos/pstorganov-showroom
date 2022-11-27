package ru.stroganov.oauth2.tokenmediatingbackend

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import ru.stroganov.oauth2.tokenmediatingbackend.config.authConfigModule
import ru.stroganov.oauth2.tokenmediatingbackend.routing.monitoringRoutingModule
import ru.stroganov.oauth2.tokenmediatingbackend.routing.oauthFlowRoutingModule
import ru.stroganov.oauth2.tokenmediatingbackend.routing.tokenMediation

fun main() {
    embeddedServer(Netty) {
        serverConfig()
        authConfigModule()
        oauthFlowRoutingModule()
        tokenMediation()
        monitoringRoutingModule()
    }.start(wait = true)
}

private fun Application.serverConfig() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
}
