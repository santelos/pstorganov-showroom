package ru.stroganov.oauth2.userauthservice.repo.hydra

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sh.ory.hydra.ApiCallback
import sh.ory.hydra.ApiClient
import sh.ory.hydra.ApiException
import sh.ory.hydra.api.AdminApi
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Configuration
class HydraConfig {

    @Bean
    fun hydraAdminClient(
        @Value("\${hydra.adminUri}") hydraAdminUri: String
    ): AdminApi = AdminApi(ApiClient().apply {
        basePath = hydraAdminUri
        addDefaultHeader("Host", "santelos.com")
    })
}
