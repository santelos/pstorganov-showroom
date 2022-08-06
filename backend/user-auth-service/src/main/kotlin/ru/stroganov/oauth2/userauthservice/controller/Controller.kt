package ru.stroganov.oauth2.userauthservice.controller

import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Flux

@RestController
class Controller(
    private val webClient: WebClient,
    private val clientRegistrationRepository: ReactiveClientRegistrationRepository
) {

    @GetMapping("/")
    suspend fun getMessages(): String {
        val resp = webClient.get()
            .uri("http://localhost:8080")
            .retrieve()
            .awaitBodilessEntity()
        return "Message 1"
    }
}
