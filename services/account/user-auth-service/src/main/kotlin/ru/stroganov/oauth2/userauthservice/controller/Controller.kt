package ru.stroganov.oauth2.userauthservice.controller

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.stroganov.oauth2.userauthservice.controller.request.AcceptConsentPostRequest
import ru.stroganov.oauth2.userauthservice.controller.response.AcceptConsentResponse
import ru.stroganov.oauth2.userauthservice.controller.response.AcceptLoginResponse
import ru.stroganov.oauth2.userauthservice.controller.response.GetConsentResponse
import ru.stroganov.oauth2.userauthservice.service.ConsentService
import ru.stroganov.oauth2.userauthservice.service.LoginAcceptService

@RestController
class Controller(
    private val loginAcceptService: LoginAcceptService,
    private val consentService: ConsentService,
) {

    @GetMapping("/accept-login")
    suspend fun acceptLogin(
        @RequestParam("login_challenge") loginChallenge: String,
        authentication: Authentication,
    ): AcceptLoginResponse = AcceptLoginResponse(loginAcceptService.acceptLogin(authentication, loginChallenge))

    @GetMapping("/accept-consent")
    suspend fun acceptConsent(
        @RequestParam("consent_challenge") consentChallenge: String,
        authentication: Authentication,
    ): AcceptConsentResponse = AcceptConsentResponse(consentService.acceptConsent(consentChallenge, emptyList()))

    @PostMapping("/accept-consent")
    suspend fun acceptConsentPost(
        @RequestParam("consent_challenge") consentChallenge: String,
        @RequestBody acceptConsentPostRequest: AcceptConsentPostRequest,
        authentication: Authentication,
    ): AcceptConsentResponse = AcceptConsentResponse(
        consentService.acceptConsent(consentChallenge, acceptConsentPostRequest.scope)
    )

    @GetMapping("/get-consent")
    suspend fun getConsent(
        @RequestParam("consent_challenge") consentChallenge: String,
        authentication: Authentication,
    ): GetConsentResponse = consentService.getConsent(consentChallenge)
        .let { GetConsentResponse(
            requestedAccessTokenAudience = it.requestedAccessTokenAudience,
            requestedScope = it.requestedScope,
            subject = it.subject,
        ) }
}
