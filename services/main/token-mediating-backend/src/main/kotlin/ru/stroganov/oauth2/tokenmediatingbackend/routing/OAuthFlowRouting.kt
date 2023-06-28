package ru.stroganov.oauth2.tokenmediatingbackend.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import ru.stroganov.oauth2.tokenmediatingbackend.AppConfig
import ru.stroganov.oauth2.tokenmediatingbackend.appConfig
import ru.stroganov.oauth2.tokenmediatingbackend.model.UserSession

fun Application.oauthFlowRoutingModule(appConf: AppConfig = appConfig) {
    routing {
        authenticate("oauth2") {
            get("/login") { }
            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                if (principal != null) {
                    call.sessions.set(UserSession(accessToken = principal.accessToken))
                    call.respondRedirect(appConf.successRedirect)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}
