package ru.stroganov.account.userauthservicek.repo.config

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.stroganov.account.userauthservicek.common.BaseException
import ru.stroganov.account.userauthservicek.common.BaseException.RepoException.Oauth2TokenException
import ru.stroganov.account.userauthservicek.config.AppConfig
import ru.stroganov.account.userauthservicek.config.appConfig

@Serializable
internal data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("scope") val scope: String,
    @SerialName("token_type") val tokenType: String,
)

internal fun BearerAuthConfig.install(config: AppConfig.Oauth2Client) {
    val bearerTokens = mutableListOf<BearerTokens>()
    loadTokens { bearerTokens.lastOrNull() }
    refreshTokens {
        val tokenInfo: TokenInfo = runCatching { client.submitForm("${config.publicUrl}/oauth2/token", parameters {
            append("grant_type", "client_credentials")
            append("client_id", config.clientId)
            append("client_secret", config.clientSecret)
            append("scope", config.scopes.joinToString(" "))
        }) {
            markAsRefreshTokenRequest()
        } }.getOrElse { throw Oauth2TokenException(
            config.clientId,
            config.publicUrl,
            it
        ) }.body()
        bearerTokens.add(BearerTokens(tokenInfo.accessToken, ""))
        bearerTokens.last()
    }
}

internal fun httpClient(config: AppConfig.Oauth2Client): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) { json() }
    install(Auth) { bearer { install(config) } }
    expectSuccess = true
}

internal val httpClient: HttpClient by lazy {
    httpClient(appConfig.oauth2Client)
}
