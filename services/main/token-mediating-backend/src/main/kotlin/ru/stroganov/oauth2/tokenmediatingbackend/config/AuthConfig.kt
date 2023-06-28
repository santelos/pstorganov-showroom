package ru.stroganov.oauth2.tokenmediatingbackend.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import ru.stroganov.oauth2.tokenmediatingbackend.*
import ru.stroganov.oauth2.tokenmediatingbackend.model.UserSession

private val oauth2flowHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.authConfigModule(
    oauth2Conf: AppConfig.Oauth2Config = appConfig.oauth2Config,
    oauth2HttpClient: HttpClient = oauth2flowHttpClient
) {
    install(Sessions) {
        cookie<UserSession>("SESSION", SessionStorageMemory())
    }
    install(Authentication) {
        oauth("oauth2") {
            urlProvider = { "${oauth2Conf.callbackUrl}/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "id-santelos",
                    authorizeUrl = "${oauth2Conf.providerUrl}/oauth2/auth",
                    accessTokenUrl = "${oauth2Conf.providerInternalUrl}/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = oauth2Conf.clientId,
                    clientSecret = oauth2Conf.clientSecret,
                    defaultScopes = listOf("asd:test")

                )
            }
            client = oauth2HttpClient
        }
        session<UserSession>("session") {
            validate {
                it.takeIf { us -> us.accessToken.isNotEmpty() }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
