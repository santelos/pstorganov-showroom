package ru.stroganov.showroom.account.userinfoservice.repo

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import ru.stroganov.showroom.account.userinfoservice.common.DatabaseException
import ru.stroganov.showroom.account.userinfoservice.service.UserId

data class UserInfoRepoResponse(
    val id: Int,
    val name: String,
)

data class UserCredentialsRepoResponse(
    val login: String,
    val passwordHash: String,
)

data class CreateUserRepoRequest(
    val login: String,
    val passwordHash: String,
    val name: String,
)

interface UsersRepo {
    suspend fun getUserInfo(userId: UserId): UserInfoRepoResponse
    suspend fun getUserCredentials(userId: UserId): UserCredentialsRepoResponse
    suspend fun createUser(newUser: CreateUserRepoRequest): UserId
}

class UsersRepoImpl(
    private val dbConnectionFactory: ConnectionFactory = dbConnectionFactoryImpl
) : UsersRepo {

    private val getUserInfoQuery = """
        SELECT id, name 
        FROM users 
        WHERE id=$1
    """.trimIndent()
    override suspend fun getUserInfo(userId: UserId): UserInfoRepoResponse = coroutineScope {
        val result = runCatching {
            dbConnectionFactory.create().awaitFirst()
                .createStatement(getUserInfoQuery)
                .bind("$1", userId.id)
                .execute().awaitFirst()
        }.getOrElse { throw DatabaseException.DriverException(it) }
        return@coroutineScope result.map { t, _ ->
            UserInfoRepoResponse(
                id = t.get("id", Integer::class.java)!!.toInt(),
                name = t.get("name", String::class.java)!!
            )
        }.awaitFirstOrNull() ?: throw DatabaseException.UserNotFoundException(userId)
    }

    private val getUserCredentialsQuery = """
        SELECT login, password_hash 
        FROM users 
        WHERE id=$1
    """.trimIndent()
    override suspend fun getUserCredentials(userId: UserId): UserCredentialsRepoResponse = coroutineScope {
        val result = runCatching {
            dbConnectionFactory.create().awaitFirst()
                .createStatement(getUserCredentialsQuery)
                .bind("$1", userId.id)
                .execute().awaitFirst()
        }.getOrElse { throw DatabaseException.DriverException(it) }
        return@coroutineScope result.map { t, _ ->
            UserCredentialsRepoResponse(
                login = t.get("login", String::class.java)!!,
                passwordHash = t.get("password_hash", String::class.java)!!
            )
        }.awaitFirstOrNull() ?: throw DatabaseException.UserNotFoundException(userId)
    }

    private val createUserQuery = """
        INSERT INTO users(login, password_hash, name)
        VALUES ($1, $2, $3)
        RETURNING id
    """.trimIndent()
    override suspend fun createUser(newUser: CreateUserRepoRequest): UserId = coroutineScope {
        val result = runCatching {
            dbConnectionFactory.create().awaitFirst()
                .createStatement(createUserQuery)
                .bind("$1", newUser.login)
                .bind("$2", newUser.passwordHash)
                .bind("$3", newUser.name)
                .execute().awaitFirst()
        }.getOrElse { throw DatabaseException.DriverException(it) }
        return@coroutineScope result.map { t, _ ->
            UserId(t.get("id", Integer::class.java)!!.toInt())
        }.awaitFirst()
    }
}
