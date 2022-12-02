package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.service.UserInfo

@Serializable
data class UserInfoResponse(
    val name: String
)

fun UserInfo.toResponse(): UserInfoResponse = UserInfoResponse(
    name = name
)
