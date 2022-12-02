package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.service.UserCredentials
import ru.stroganov.showroom.account.userinfoservice.service.UserCredentialsValidation

@Serializable
data class VerifyCredentialsRequest(
    val login: String,
    val password: String,
)

@Serializable
data class VerifyCredentialsResponse(
    val isValid: Boolean,
    val errors: List<String>,
)

fun VerifyCredentialsRequest.toService(): UserCredentials = UserCredentials(
    login = login,
    password = password,
)

fun UserCredentialsValidation.toResponse(): VerifyCredentialsResponse = when(this) {
    is UserCredentialsValidation.Invalid -> VerifyCredentialsResponse(
        isValid = false,
        errors = errors
    )
    UserCredentialsValidation.Valid -> VerifyCredentialsResponse(
        isValid = true,
        errors = emptyList()
    )
}
