package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.service.UserCredentials
import ru.stroganov.showroom.account.userinfoservice.service.UserAuthInfo
import ru.stroganov.showroom.account.userinfoservice.service.UserLogin

@Serializable
data class GetUserAuthInfoRequest(
    val login: String,
    val password: String,
)

@Serializable
data class UserAuthResponse(
    val isValid: Boolean,
    val errors: List<String>,
    val userId: Int?,
)

fun GetUserAuthInfoRequest.toService(): UserCredentials = UserCredentials(
    login = UserLogin(login),
    password = password,
)

fun UserAuthInfo.toResponse(): UserAuthResponse = when(this) {
    is UserAuthInfo.Invalid -> UserAuthResponse(
        isValid = false,
        errors = errors,
        userId = null,
    )
    is UserAuthInfo.Success -> UserAuthResponse(
        isValid = true,
        errors = emptyList(),
        userId = userId.id,
    )
}
