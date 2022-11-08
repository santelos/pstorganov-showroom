package ru.stroganov.oauth2.userauthservice.repo.hydra.response

data class GetConsentRepoResponse(
    val requestedAccessTokenAudience: List<String>?,
    val requestedScope: List<String>?,
    val subject: String?,
)
