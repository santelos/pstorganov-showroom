package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPublicInfoRequest(
    val id: Int
)

@Serializable
data class UserPublicInfoResponse(
    val name: String
)
