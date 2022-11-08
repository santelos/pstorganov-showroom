package ru.stroganov.oauth2.userauthservice.controller.response

data class GetConsentResponse(
    val requestedAccessTokenAudience: List<String>?,
    val requestedScope: List<String>?,
    val subject: String?,
)
