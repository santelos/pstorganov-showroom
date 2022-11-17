package ru.stroganov.showroom.account.userinfoservice.common

import io.konform.validation.ValidationErrors
import ru.stroganov.showroom.account.userinfoservice.service.UserId

class ValidationException(
    errors: ValidationErrors
) : RuntimeException(errors.toJson())

sealed class DatabaseException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause) {
    class DriverException(ex: Throwable) : DatabaseException("Internal DB error [${ex.message}]", ex)
    class UserNotFoundException(userId: UserId) : DatabaseException("User not found with ID=[${userId.id}]")
}
