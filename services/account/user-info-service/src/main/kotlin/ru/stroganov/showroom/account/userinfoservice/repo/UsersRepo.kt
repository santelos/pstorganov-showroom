package ru.stroganov.showroom.account.userinfoservice.repo

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import ru.stroganov.showroom.account.userinfoservice.common.DatabaseException
import ru.stroganov.showroom.account.userinfoservice.service.UserId
import ru.stroganov.showroom.account.userinfoservice.service.UserLogin

sealed interface UserInfoRepoResponse{
    data class Success(val id: Int, val name: String) : UserInfoRepoResponse
    object UserNotFound : UserInfoRepoResponse
}

sealed interface GetPasswordHashResponse {
    data class Success(val passwordHash: String) : GetPasswordHashResponse
    object UserNotFound : GetPasswordHashResponse
}

data class CreateUserRepoRequest(
    val login: String,
    val passwordHash: String,
    val name: String,
)

data class ValidatePasswordRequest(
    val login: String,
    val passwordHash: String,
)

interface UsersRepo {
    suspend fun getUserInfo(userId: UserId): UserInfoRepoResponse
    suspend fun getPasswordHash(login: UserLogin): GetPasswordHashResponse
    suspend fun createUser(newUser: CreateUserRepoRequest): UserId
}

internal object UsersRepoObject : UsersRepo by UsersRepoImpl()
internal class UsersRepoImpl(
    private val connectionFactory: ConnectionFactory = r2dbcConnectionFactory
) : UsersRepo {

    private val getUserInfoQuery = """
        SELECT id, name 
        FROM users 
        WHERE id=$1
    """.trimIndent()
    override suspend fun getUserInfo(userId: UserId): UserInfoRepoResponse = runCatching {
        connectionFactory.create().awaitFirst()
            .createStatement(getUserInfoQuery)
            .bind("$1", userId.id)
            .execute().awaitFirst()
    }.getOrElse { throw DatabaseException.DriverException(it) }
        .map { t, _ ->
            UserInfoRepoResponse.Success(
                id = t.get("id", Integer::class.java)!!.toInt(),
                name = t.get("name", String::class.java)!!
            )
        }.awaitFirstOrNull() ?: UserInfoRepoResponse.UserNotFound


    private val validatePasswordQuery = """
        SELECT password_hash 
        FROM users 
        WHERE login=$1
    """.trimIndent()
    override suspend fun getPasswordHash(login: UserLogin): GetPasswordHashResponse = runCatching {
        connectionFactory.create().awaitFirst()
            .createStatement(validatePasswordQuery)
            .bind("$1", login.login)
            .execute().awaitFirst()
    }.getOrElse { throw DatabaseException.DriverException(it) }
        .map { t, _ ->
            GetPasswordHashResponse.Success(t.get("password_hash", String::class.java)!!)
        }
        .awaitFirstOrNull() ?: GetPasswordHashResponse.UserNotFound


    private val createUserQuery = """
        INSERT INTO users(login, password_hash, name)
        VALUES ($1, $2, $3)
        RETURNING id
    """.trimIndent()
    override suspend fun createUser(newUser: CreateUserRepoRequest): UserId = runCatching {
        connectionFactory.create().awaitFirst()
            .createStatement(createUserQuery)
            .bind("$1", newUser.login)
            .bind("$2", newUser.passwordHash)
            .bind("$3", newUser.name)
            .execute().awaitFirst()
    }.getOrElse { throw DatabaseException.DriverException(it) }
        .map { t, _ ->
            UserId(t.get("id", Integer::class.java)!!.toInt())
        }.awaitFirst()
}
