package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.service.NewUser
import ru.stroganov.showroom.account.userinfoservice.service.UserId

@Serializable
data class NewUserRequest(
    val login: String,
    val password: String,
    val name: String,
    val roles: Set<String>,
)

@Serializable
data class NewUserResponse(
    val userId: Int,
)
