package ru.stroganov.showroom.account.userinfoservice.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import ru.stroganov.showroom.account.userinfoservice.AppConfig
import ru.stroganov.showroom.account.userinfoservice.appConfig
import ru.stroganov.showroom.account.userinfoservice.common.OAuth2Principal
import ru.stroganov.showroom.account.userinfoservice.common.oauth2ResourceServer

private val oauth2HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.authConfig(
    oauth2Config: AppConfig.Oauth2Config = appConfig.oauth2
) {
    install(Authentication) {
        oauth2ResourceServer("main") {
            client = oauth2HttpClient
            tokenEndpoint = "${oauth2Config.adminUri}/oauth2/introspect"
        }
    }
}

fun principalToRolesMapping(principal: Principal): Set<String>? = (principal as OAuth2Principal)
    .scope?.split(Regex("\\s"))?.toSet()

fun principalToUserId(principal: Principal): String? = (principal as OAuth2Principal)
    .sub
