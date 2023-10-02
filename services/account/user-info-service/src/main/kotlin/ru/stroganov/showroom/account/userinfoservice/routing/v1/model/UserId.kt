package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import io.konform.validation.Validation
import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.common.ValidationException
import ru.stroganov.showroom.account.userinfoservice.common.isNumber
import ru.stroganov.showroom.account.userinfoservice.service.UserId

@Serializable
data class UserIdRequest(
    val login: String
)

@Serializable
data class UserIdResponse(
    val userId: Int
)
