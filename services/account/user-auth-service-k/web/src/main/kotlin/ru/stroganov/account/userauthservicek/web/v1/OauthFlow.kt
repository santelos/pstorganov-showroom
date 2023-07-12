package ru.stroganov.account.userauthservicek.web.v1

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import ru.stroganov.account.userauthservicek.common.UserId
import ru.stroganov.account.userauthservicek.service.ConsentService
import ru.stroganov.account.userauthservicek.service.LoginService
import ru.stroganov.account.userauthservicek.service.consentServiceImpl
import ru.stroganov.account.userauthservicek.service.loginServiceImpl
import ru.stroganov.account.userauthservicek.web.config.BASIC_AUTH

@Serializable
data class AcceptLoginResponse(
    val redirectTo: String,
)

@Serializable
data class AcceptConsentPostRequest(
    val scope: List<String>
)

@Serializable
data class AcceptConsentResponse(
    val redirectTo: String,
)

@Serializable
data class GetConsentResponse(
    val requestedAccessTokenAudience: List<String>?,
    val requestedScope: List<String>?,
    val subject: String?,
)

internal fun Application.oauthFlow(
    loginService: LoginService = loginServiceImpl,
    consentService: ConsentService = consentServiceImpl,
) {
    routing {
        authenticate(BASIC_AUTH) {
            get("/accept-login") {
                val loginChallenge = call.parameters.getOrFail("login_challenge")
                val userId = UserId(call.principal<UserIdPrincipal>()!!.name.toInt())
                val response = loginService.acceptLogin(userId, loginChallenge)
                call.respond(AcceptLoginResponse(response))
            }
            get("/accept-consent") {
                val consentChallenge = call.parameters.getOrFail("consent_challenge")
                val response = consentService.acceptConsent(consentChallenge, emptyList())
                call.respond(AcceptConsentResponse(response))
            }
            post("/accept-consent") {
                val consentChallenge = call.parameters.getOrFail("consent_challenge")
                val requestBody = call.receive<AcceptConsentPostRequest>()
                val response = consentService.acceptConsent(consentChallenge, requestBody.scope)
                call.respond(AcceptConsentResponse(response))
            }
            get("/get-consent") {
                val consentChallenge = call.parameters.getOrFail("consent_challenge")
                val response = consentService.getConsent(consentChallenge)
                call.respond(GetConsentResponse(
                    response.requestedAccessTokenAudience,
                    response.requestedScope,
                    response.subject,
                ))
            }
        }
    }
}
