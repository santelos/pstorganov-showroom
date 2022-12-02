package ru.stroganov.oauth2.userauthservice.controller.response

import ru.stroganov.oauth2.userauthservice.service.model.UserId

data class UserIdResponse(
    val id: Int,
)

fun UserId.toResponse(): UserIdResponse = UserIdResponse(id)
