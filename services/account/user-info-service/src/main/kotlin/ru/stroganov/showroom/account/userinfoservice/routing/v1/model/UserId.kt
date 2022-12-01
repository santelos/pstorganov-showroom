package ru.stroganov.showroom.account.userinfoservice.routing.v1.model

import io.konform.validation.Validation
import kotlinx.serialization.Serializable
import ru.stroganov.showroom.account.userinfoservice.common.ValidationException
import ru.stroganov.showroom.account.userinfoservice.common.isNumber
import ru.stroganov.showroom.account.userinfoservice.service.UserId

@Serializable
data class UserIdRequest(
    val userId: String
)

@Serializable
data class UserIdResponse(
    val userId: Int
)

val userIdRequestValidation = Validation {
    UserIdRequest::userId {
        isNumber()
    }
}

fun UserIdRequest.toUserId(): UserId {
    val validation = userIdRequestValidation(this)
    if (validation.errors.isNotEmpty()) {
        throw ValidationException(validation.errors)
    }
    return UserId(id = userId.toInt())
}

fun UserId.toResponse(): UserIdResponse = UserIdResponse(id)
