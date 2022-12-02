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
import ru.stroganov.oauth2.tokenmediatingbackend.OAUTH2__CALLBACK_URL
import ru.stroganov.oauth2.tokenmediatingbackend.OAUTH2__CLIENT_ID
import ru.stroganov.oauth2.tokenmediatingbackend.OAUTH2__CLIENT_SECRET
import ru.stroganov.oauth2.tokenmediatingbackend.OAUTH2__PROVIDER_URL
import ru.stroganov.oauth2.tokenmediatingbackend.model.UserSession

private val oauth2flowHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.authConfigModule(oauth2HttpClient: HttpClient = oauth2flowHttpClient) {
    install(Sessions) {
        cookie<UserSession>("SESSION", SessionStorageMemory())
    }
    install(Authentication) {
        oauth("oauth2") {
            urlProvider = { "$OAUTH2__CALLBACK_URL/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "id-santelos",
                    authorizeUrl = "$OAUTH2__PROVIDER_URL/oauth2/auth",
                    accessTokenUrl = "$OAUTH2__PROVIDER_URL/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = OAUTH2__CLIENT_ID,
                    clientSecret = OAUTH2__CLIENT_SECRET,
                    defaultScopes = listOf("asd:test")

                )
            }
            client = oauth2HttpClient
        }
        session<UserSession>("session") {
            validate {
                if (it.accessToken.isNotEmpty()) {
                    it
                } else {
                    null
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
