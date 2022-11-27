package ru.stroganov.oauth2.userauthservice.repo.hydra

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sh.ory.hydra.ApiClient
import sh.ory.hydra.api.AdminApi

@Configuration
class HydraConfig {

    @Bean
    fun hydraClient(
        @Value("\${hydra.adminUri}") hydraAdminUri: String
    ): AdminApi = AdminApi(ApiClient().apply {
        basePath = hydraAdminUri
        addDefaultHeader("Host", "santelos.com")
    })
}
