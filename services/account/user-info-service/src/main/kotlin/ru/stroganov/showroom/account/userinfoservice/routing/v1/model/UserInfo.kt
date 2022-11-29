package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import ru.stroganov.showroom.account.userinfoservice.service.UserInfo

data class UserInfoResponse(
    val name: String
)

fun UserInfo.toResponse(): UserInfoResponse = UserInfoResponse(
    name = name
)
