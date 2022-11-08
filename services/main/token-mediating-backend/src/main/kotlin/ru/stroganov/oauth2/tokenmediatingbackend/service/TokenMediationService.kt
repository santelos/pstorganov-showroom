package ru.stroganov.oauth2.tokenmediatingbackend.service

import ru.stroganov.oauth2.tokenmediatingbackend.model.UserSession

data class AuthToken(
    val accessToken: String
)

interface TokenMediationService {
    suspend fun getToken(auth: UserSession): AuthToken
}

internal class TokenMediationServiceImpl : TokenMediationService {
    override suspend fun getToken(auth: UserSession): AuthToken {
        return AuthToken(auth.accessToken)
    }
}
