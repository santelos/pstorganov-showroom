package ru.stroganov.showroom.account.userinfoservice.common

import io.konform.validation.ValidationErrors
import ru.stroganov.showroom.account.userinfoservice.service.UserId
import ru.stroganov.showroom.account.userinfoservice.service.UserLogin

class ValidationException(
    errors: ValidationErrors
) : RuntimeException(errors.toJson())

sealed class DatabaseException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause) {
    data class DriverException(val ex: Throwable) : DatabaseException("Internal DB error [${ex.message}]", ex)
}

sealed class ServiceException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause) {
    data class UserIdNotFoundException(val userId: UserId) : ServiceException("User not found with ID=[${userId.id}]")
    data class UserLoginNotFound(val userLogin: UserLogin) : ServiceException("User not found with LOGIN=[${userLogin.login}]")
}
