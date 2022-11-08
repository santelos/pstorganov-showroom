package ru.stroganov.oauth2.userauthservice.controller

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.reactive.function.client.toEntity
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import ru.stroganov.oauth2.userauthservice.controller.response.AcceptConsentResponse
import ru.stroganov.oauth2.userauthservice.controller.response.AcceptLoginResponse
import ru.stroganov.oauth2.userauthservice.controller.response.GetConsentResponse
import ru.stroganov.oauth2.userauthservice.service.ConsentService
import ru.stroganov.oauth2.userauthservice.service.LoginAcceptService
import java.net.URI

@RestController
class Controller(
    private val loginAcceptService: LoginAcceptService,
    private val consentService: ConsentService,
) {

    @GetMapping("/accept-login")
    suspend fun acceptLogin(
        @RequestParam("login_challenge") loginChallenge: String,
        authentication: Authentication,
    ): AcceptLoginResponse = AcceptLoginResponse(loginAcceptService.acceptLogin(loginChallenge, authentication.name))

    @GetMapping("/accept-consent")
    suspend fun acceptConsent(
        @RequestParam("consent_challenge") consentChallenge: String,
        authentication: Authentication,
    ): AcceptConsentResponse = AcceptConsentResponse(consentService.acceptConsent(consentChallenge))

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
