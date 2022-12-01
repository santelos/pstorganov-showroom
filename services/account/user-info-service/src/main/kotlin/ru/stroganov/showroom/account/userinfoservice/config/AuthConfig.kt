package ru.stroganov.showroom.account.userinfoservice.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import ru.stroganov.showroom.account.userinfoservice.OAUTH2__ADMIN_URI
import ru.stroganov.showroom.account.userinfoservice.common.OAuth2Principal
import ru.stroganov.showroom.account.userinfoservice.common.oauth2ResourceServer

private val oauth2HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.authConfig() {
    install(Authentication) {
        oauth2ResourceServer("main") {
            client = oauth2HttpClient
            tokenEndpoint = "$OAUTH2__ADMIN_URI/oauth2/introspect"
        }
    }
}

fun principalToRolesMapping(principal: Principal): Set<String> = (principal as OAuth2Principal)
    .scope?.split(Regex("\\s"))?.toSet() ?: emptySet()
