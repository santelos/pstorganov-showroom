package ru.stroganov.account.userauthservicek.web.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import ru.stroganov.account.userauthservicek.config.AppConfig
import ru.stroganov.account.userauthservicek.config.appConfig
import ru.stroganov.account.userauthservicek.service.RemoteAuthentication
import ru.stroganov.account.userauthservicek.service.UserAuthInfo
import ru.stroganov.account.userauthservicek.service.remoteAuthenticationImpl

internal const val OAUTH2_AUTH = "oauth2"
internal const val BASIC_AUTH = "basic"

private val oauth2HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

internal fun Application.authConfig(
    config: AppConfig.HydraClient = appConfig.hydra,
    remoteAuthentication: RemoteAuthentication = remoteAuthenticationImpl
) {
    install(Authentication) {
        oauth2ResourceServer(OAUTH2_AUTH) {
            client = oauth2HttpClient
            tokenEndpoint = "${config.adminUrl}/oauth2/introspect"
        }
        basic(BASIC_AUTH) {
            validate {cred ->
                when (val authInfo = remoteAuthentication.getUserAuthInfo(cred.name, cred.password)) {
                    is UserAuthInfo.Success -> UserIdPrincipal(authInfo.userId.id.toString())
                    is UserAuthInfo.AuthFailed -> null
                }
            }
        }
    }
}
