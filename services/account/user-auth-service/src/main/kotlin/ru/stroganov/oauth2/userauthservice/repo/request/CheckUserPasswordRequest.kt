package ru.stroganov.oauth2.userauthservice.repo.request

data class CheckUserPasswordRequest(
    val username: String,
    val passwordHash: String,
)
