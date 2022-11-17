package ru.stroganov.showroom.account.userinfoservice.service

import ru.stroganov.showroom.account.userinfoservice.repo.CreateUserRepoRequest
import ru.stroganov.showroom.account.userinfoservice.repo.UsersRepo
import ru.stroganov.showroom.account.userinfoservice.repo.UsersRepoImpl

data class UserId(
    val id: Int
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
    val passwordHash: String,
)

interface UserService {
    suspend fun createUser(newUser: NewUser): UserId
    suspend fun getUserInfo(userId: UserId): UserInfo
    suspend fun getUserCredentials(userId: UserId): UserCredentials
}

private val usersRepoImpl = UsersRepoImpl()
private val hashingImpl = HashingImpl()

internal class UserServiceImpl(
    private val usersRepo: UsersRepo = usersRepoImpl,
    private val hashing: Hashing = hashingImpl,
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

    override suspend fun getUserInfo(userId: UserId): UserInfo {
        val result = usersRepo.getUserInfo(userId)
        return UserInfo(
            id = result.id,
            name = result.name
        )
    }

    override suspend fun getUserCredentials(userId: UserId): UserCredentials {
        val result = usersRepo.getUserCredentials(userId)
        return UserCredentials(
            login = result.login,
            passwordHash = result.passwordHash
        )
    }
}
