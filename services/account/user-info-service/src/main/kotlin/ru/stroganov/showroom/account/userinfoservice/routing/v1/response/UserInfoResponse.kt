package ru.stroganov.showroom.account.userinfoservice.routing.v1.response

import ru.stroganov.showroom.account.userinfoservice.service.UserInfo

data class UserInfoResponse(
    val name: String
)

fun UserInfo.toUserInfoResponse(): UserInfoResponse = UserInfoResponse(
    name = name
)
