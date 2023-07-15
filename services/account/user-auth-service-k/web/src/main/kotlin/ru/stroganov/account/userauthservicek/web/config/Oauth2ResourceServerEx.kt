package ru.stroganov.account.userauthservicek.web.config

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class OAuth2Principal(
    val token: String,
    val active: Boolean,
    val scope: String?,
    val clientId: String?,
    val username: String?,
    val tokenType: String?,
    val exp: Int?,
    val iat: Int?,
    val nbf: Int?,
    val sub: String?,
    val aud: List<String>,
    val iss: String?,
    val jti: String?
) : Principal

fun AuthenticationConfig.oauth2ResourceServer(
    name: String? = null,
    configure: OAuth2ResourceServerProvider.Config.() -> Unit
) {
    val provider = OAuth2ResourceServerProvider.Config(name).apply(configure).build()
    register(provider)
}

class OAuth2ResourceServerProvider(val config: Config) : AuthenticationProvider(config) {

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val authHeaderValue = context.call.request.bearerHeaderValue()
        if (authHeaderValue == null) {
            context.error(OAuthKey, AuthenticationFailedCause.NoCredentials)
        } else {
            when (val tokenIntrospection = tokenIntrospection(authHeaderValue, null)) {
                is TokenIntrospectionResponse.Error ->
                    context.error(OAuthKey, tokenIntrospection.authenticationFailedCause())
                is TokenIntrospectionResponse.Success ->
                    context.principal(tokenIntrospection.principal(authHeaderValue))
            }
        }
    }

    class Config(name: String?) : AuthenticationProvider.Config(name) {
        lateinit var client: HttpClient
        lateinit var tokenEndpoint: String
        internal fun build() = OAuth2ResourceServerProvider(this)
    }
}

sealed interface TokenIntrospectionResponse {

    /**
     * https://www.rfc-editor.org/rfc/rfc7662#section-2.2
     */
    @Serializable
    data class Success(
        val active: Boolean,
        val aud: List<String> = emptyList(),
        @SerialName("client_id") val clientId: String? = null,
        val exp: Int? = null,
        val ext: Map<String, String?> = emptyMap(),
        val iat: Int? = null,
        val iss: String? = null,
        val nbf: Int? = null,
        @SerialName("obfuscated_subject") val obfuscatedSubject: String? = null,
        val scope: String? = null,
        val sub: String? = null,
        @SerialName("token_type") val tokenType: String? = null,
        @SerialName("token_use") val tokenUse: String? = null,
        val username: String? = null
    ) : TokenIntrospectionResponse, Principal

    @Serializable
    data class Error(
        val error: String,
        val errorDebug: String,
        val errorDescription: String,
        val errorHint: String,
        val statusCode: Int
    ) : TokenIntrospectionResponse
}
private suspend fun OAuth2ResourceServerProvider.tokenIntrospection(
    token: String,
    scope: String?
): TokenIntrospectionResponse {
    val request = Parameters.build {
        append("token", token)
        scope?.let { append("scope", scope) }
    }
    val httpResponse = config.client.post(config.tokenEndpoint) {
        setBody(
            TextContent(
                request.formUrlEncode(),
                ContentType.Application.FormUrlEncoded
            )
        )
    }
    if (!httpResponse.status.isSuccess()) {
        return httpResponse.body<TokenIntrospectionResponse.Error>()
    }
    return httpResponse.body<TokenIntrospectionResponse.Success>()
}

private fun TokenIntrospectionResponse.Error.authenticationFailedCause(): AuthenticationFailedCause =
    if (statusCode != 500) {
        AuthenticationFailedCause.InvalidCredentials
    } else {
        AuthenticationFailedCause.Error(errorDescription)
    }

private fun TokenIntrospectionResponse.Success.principal(token: String): OAuth2Principal = OAuth2Principal(
    token = token,
    active = active,
    scope = scope,
    clientId = clientId,
    username = username,
    tokenType = tokenType,
    exp = exp,
    iat = iat,
    nbf = nbf,
    sub = sub,
    aud = aud,
    iss = iss,
    jti = null
)

private fun ApplicationRequest.bearerHeaderValue(): String? =
    when (val header = parseAuthorizationHeader()) {
        is HttpAuthHeader.Single -> {
            if (header.authScheme.startsWith("Bearer", ignoreCase = true)) {
                header.blob
            } else {
                null
            }
        }
        else -> null
    }
