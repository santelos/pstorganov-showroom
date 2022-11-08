package ru.stroganov.oauth2.tokenmediatingbackend.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import ru.stroganov.oauth2.tokenmediatingbackend.model.UserSession
import ru.stroganov.oauth2.tokenmediatingbackend.service.TokenMediationService
import ru.stroganov.oauth2.tokenmediatingbackend.service.TokenMediationServiceImpl

private val tokenMediationServiceImpl = TokenMediationServiceImpl()

@Serializable
data class GetTokenResponse(
    val accessToken: String
)

fun Application.tokenMediation(
    tokenMediationService: TokenMediationService = tokenMediationServiceImpl
) {
    routing {
        authenticate("session") {
            get("/token") {
                val auth: UserSession? = call.sessions.get()
                if (auth != null) {
                    val result = tokenMediationService.getToken(auth)
                    val response = GetTokenResponse(
                        accessToken = result.accessToken
                    )
                    call.respond(response)
                } else {
                    throw RuntimeException("Passed session auth, but no session presented")
                }
            }
            post("/logout") {
                call.sessions.clear<UserSession>()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
