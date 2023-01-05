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
    val login: UserLogin,
    val password: String,
)

sealed interface UserAuthInfo {
    data class Invalid(val errors: List<String>) : UserAuthInfo
    data class Success(val userId: UserId) : UserAuthInfo
}

interface UserService {
    suspend fun createUser(newUser: NewUser): UserId
    suspend fun getUserInfo(userId: UserId): UserInfo
    suspend fun getUserAuthInfo(credentials: UserCredentials): UserAuthInfo
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

    override suspend fun getUserAuthInfo(credentials: UserCredentials): UserAuthInfo =
        when(val dbPasswordHash = usersRepo.getPasswordHash(credentials.login)) {
            is GetPasswordHashResponse.Success -> {
                if (hashing.isEqual(credentials.password, dbPasswordHash.passwordHash)) {
                    when(val userId = usersRepo.getUserId(credentials.login)) {
                        is UserIdRepoResponse.Success -> UserAuthInfo.Success(UserId(userId.id))
                        UserIdRepoResponse.UserNotFound -> throw ServiceException.UserLoginNotFound(credentials.login)
                    }
                } else {
                    UserAuthInfo.Invalid(listOf("Password doesn't match"))
                }
            }
            GetPasswordHashResponse.UserNotFound -> UserAuthInfo.Invalid(listOf("User not found"))
        }
}
