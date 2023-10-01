package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.service.NewUser

@Serializable
data class NewUserRequest(
    val login: String,
    val password: String,
    val name: String
)

fun NewUserRequest.toService(): NewUser = NewUser(
    login = login,
    password = password,
    name = name,
    roles = emptySet(),
)
