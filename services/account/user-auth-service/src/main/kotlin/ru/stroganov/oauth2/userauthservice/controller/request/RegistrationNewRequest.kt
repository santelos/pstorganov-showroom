package ru.stroganov.oauth2.userauthservice.controller.request

import ru.stroganov.oauth2.userauthservice.service.RegistrationNewServiceRequest

data class RegistrationNewRequest(
    val login: String,
    val password: String,
    val name: String,
)

fun RegistrationNewRequest.toService(): RegistrationNewServiceRequest = RegistrationNewServiceRequest(
    login = login,
    password = password,
    name = name,
)
