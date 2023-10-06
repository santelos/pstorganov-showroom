package ru.stroganov.showroom.account.userinfoservice.service

import ru.stroganov.showroom.account.userinfoservice.common.ServiceException
import ru.stroganov.showroom.account.userinfoservice.repo.*
import ru.stroganov.showroom.account.userinfoservice.repo.UsersRepoObject
import ru.stroganov.showroom.account.userinfoservice.routing.v1.model.GetUserAuthInfoResponse

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
    val roles: Set<String>,
)

data class UserPublicInfo(
    val name: String,
)

data class UserCredentials(
    val login: UserLogin,
    val password: String,
)

data class UserAuthInfo(
    val passwordHash: String,
    val roles: Set<String>,
)

interface UserService {
    suspend fun getUserId(userLogin: UserLogin): UserId
    suspend fun getUserAuthInfo(userId: UserId): UserAuthInfo
    suspend fun getUserPublicInfo(userId: UserId): UserPublicInfo
    suspend fun createUser(newUser: NewUser): UserId
}

internal object UserServiceObject : UserService by UserServiceImpl(
    usersRepo = UsersRepoObject,
    hashing = HashingObject,
)
internal class UserServiceImpl(
    private val usersRepo: UsersRepo,
    private val hashing: Hashing,
) : UserService {

    override suspend fun getUserId(userLogin: UserLogin): UserId =
        when(val result = usersRepo.getUserId(userLogin)) {
            is UserIdRepoResponse.Success -> result.id
            UserIdRepoResponse.UserNotFound -> throw ServiceException.UserLoginNotFound(userLogin)
        }

    override suspend fun getUserAuthInfo(userId: UserId): UserAuthInfo =
        when(val userAuthInfo = usersRepo.getUserAuthInfo(userId)) {
            is UserAuthInfoRepoResponse.Success -> UserAuthInfo(
                passwordHash = userAuthInfo.passwordHash,
                roles = userAuthInfo.roles
            )
            UserAuthInfoRepoResponse.UserNotFound -> throw ServiceException.UserIdNotFoundException(userId)
        }

    override suspend fun getUserPublicInfo(userId: UserId): UserPublicInfo =
        when(val result = usersRepo.getUserPublicInfo(userId)) {
            is UserPublicInfoRepoResponse.Success -> UserPublicInfo(
                name = result.name
            )
            UserPublicInfoRepoResponse.UserNotFound -> throw ServiceException.UserIdNotFoundException(userId)
        }

    override suspend fun createUser(newUser: NewUser): UserId {
        val passwordHash = hashing.hash(newUser.password)
        val createUserRepoRequest = CreateUserRepoRequest(
            login = newUser.login,
            passwordHash = passwordHash,
            name = newUser.name,
            roles = newUser.roles
        )
        return usersRepo.createUser(createUserRepoRequest)
    }
}
