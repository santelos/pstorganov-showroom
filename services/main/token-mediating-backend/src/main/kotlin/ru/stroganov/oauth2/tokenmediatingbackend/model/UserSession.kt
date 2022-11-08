package ru.stroganov.oauth2.tokenmediatingbackend.model

import io.ktor.server.auth.*

data class UserSession(
    val accessToken: String,
) : Principal
