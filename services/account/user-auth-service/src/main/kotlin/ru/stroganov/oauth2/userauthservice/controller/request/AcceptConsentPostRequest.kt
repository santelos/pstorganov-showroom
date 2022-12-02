package ru.stroganov.oauth2.userauthservice.controller.request

data class AcceptConsentPostRequest(
    val scope: List<String>
)
