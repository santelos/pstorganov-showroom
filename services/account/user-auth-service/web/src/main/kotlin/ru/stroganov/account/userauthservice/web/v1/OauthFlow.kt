package ru.stroganov.account.userauthservice.web.v1

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import ru.stroganov.account.userauthservice.common.UserId
import ru.stroganov.account.userauthservice.service.ConsentService
import ru.stroganov.account.userauthservice.service.LoginService
import ru.stroganov.account.userauthservice.service.consentServiceImpl
import ru.stroganov.account.userauthservice.service.loginServiceImpl
import ru.stroganov.account.userauthservice.web.config.BASIC_AUTH

@Serializable
internal data class AcceptLoginWebResponse(
    val redirectTo: String
)

@Serializable
internal data class AcceptConsentPostWebRequest(
    val scope: List<String>
)

@Serializable
internal data class AcceptConsentWebResponse(
    val redirectTo: String
)

@Serializable
internal data class GetConsentWebResponse(
    val requestedAccessTokenAudience: List<String>?,
    val requestedScope: List<String>?,
    val subject: String?
)

internal fun Application.oauthFlow(
    loginService: LoginService = loginServiceImpl,
    consentService: ConsentService = consentServiceImpl
) {
    routing {
        authenticate(BASIC_AUTH) {
            get("/accept-login") {
                val loginChallenge = call.parameters.getOrFail("login_challenge")
                val userId = UserId(call.principal<UserIdPrincipal>()!!.name.toInt())
                val response = loginService.acceptLogin(userId, loginChallenge)
                call.respond(AcceptLoginWebResponse(response))
            }
            get("/accept-consent") {
                val consentChallenge = call.parameters.getOrFail("consent_challenge")
                val response = consentService.acceptConsent(consentChallenge, emptyList())
                call.respond(AcceptConsentWebResponse(response))
            }
            post("/accept-consent") {
                val consentChallenge = call.parameters.getOrFail("consent_challenge")
                val requestBody = call.receive<AcceptConsentPostWebRequest>()
                val response = consentService.acceptConsent(consentChallenge, requestBody.scope)
                call.respond(AcceptConsentWebResponse(response))
            }
            get("/get-consent") {
                val consentChallenge = call.parameters.getOrFail("consent_challenge")
                val response = consentService.getConsent(consentChallenge)
                call.respond(
                    GetConsentWebResponse(
                        response.requestedAccessTokenAudience,
                        response.requestedScope,
                        response.subject
                    )
                )
            }
        }
    }
}
