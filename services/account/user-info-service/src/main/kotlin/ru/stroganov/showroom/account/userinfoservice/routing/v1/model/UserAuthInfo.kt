package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.service.UserCredentials
import ru.stroganov.showroom.account.userinfoservice.service.UserAuthInfo
import ru.stroganov.showroom.account.userinfoservice.service.UserId
import ru.stroganov.showroom.account.userinfoservice.service.UserLogin

@Serializable
data class GetUserAuthInfoRequest(
    val userId: Int
)

@Serializable
data class GetUserAuthInfoResponse(
    val passwordHash: String,
    val roles: Set<String>,
)
