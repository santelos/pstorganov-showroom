package ru.stroganov.showroom.account.userinfoservice.service

import ru.stroganov.showroom.account.userinfoservice.common.ServiceException
import ru.stroganov.showroom.account.userinfoservice.repo.*
import ru.stroganov.showroom.account.userinfoservice.repo.UsersRepoObject

data class UserId(
    val id: Int
)

data class UserLogin(
    val login: String
)

data class NewUser(
    val login: String,
    val password: String,
    val name: String,
)

data class UserInfo(
    val id: Int,
    val name: String,
)

data class UserCredentials(
    val login: String,
    val password: String,
)

sealed interface UserCredentialsValidation {
    object Valid : UserCredentialsValidation
    data class Invalid(val errors: List<String>) : UserCredentialsValidation
}

interface UserService {
    suspend fun createUser(newUser: NewUser): UserId
    suspend fun getUserInfo(userId: UserId): UserInfo
    suspend fun validateCredentials(credentials: UserCredentials): UserCredentialsValidation
}

internal object UserServiceObject : UserService by UserServiceImpl()
internal class UserServiceImpl(
    private val usersRepo: UsersRepo = UsersRepoObject,
    private val hashing: Hashing = HashingObject,
) : UserService {
    override suspend fun createUser(newUser: NewUser): UserId {
        val passwordHash = hashing.hash(newUser.password)
        val createUserRepoRequest = CreateUserRepoRequest(
            login = newUser.login,
            passwordHash = passwordHash,
            name = newUser.name
        )
        return usersRepo.createUser(createUserRepoRequest)
    }

    override suspend fun getUserInfo(userId: UserId): UserInfo =
        when(val result = usersRepo.getUserInfo(userId)) {
            is UserInfoRepoResponse.Success -> UserInfo(
                id = result.id,
                name = result.name
            )
            UserInfoRepoResponse.UserNotFound -> throw ServiceException.UserIdNotFoundException(userId)
        }

    override suspend fun validateCredentials(credentials: UserCredentials): UserCredentialsValidation =
        when(val dbPasswordHash = usersRepo.getPasswordHash(UserLogin(credentials.login))) {
            is GetPasswordHashResponse.Success -> {
                if (hashing.isEqual(credentials.password, dbPasswordHash.passwordHash)) {
                    UserCredentialsValidation.Valid
                } else {
                    UserCredentialsValidation.Invalid(listOf("Password doesn't match"))
                }
            }
            GetPasswordHashResponse.UserNotFound -> UserCredentialsValidation.Invalid(listOf("User not found"))
        }
}
