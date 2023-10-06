package ru.stroganov.showroom.account.userinfoservice.repo

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import ru.stroganov.showroom.account.userinfoservice.common.DatabaseException
import ru.stroganov.showroom.account.userinfoservice.service.UserId
import ru.stroganov.showroom.account.userinfoservice.service.UserLogin

sealed interface UserIdRepoResponse {
    data class Success(val id: UserId) : UserIdRepoResponse
    object UserNotFound : UserIdRepoResponse
}

sealed interface UserAuthInfoRepoResponse {
    data class Success(val passwordHash: String, val roles: Set<String>) : UserAuthInfoRepoResponse
    object UserNotFound : UserAuthInfoRepoResponse
}

sealed interface UserPublicInfoRepoResponse {
    data class Success(val name: String) : UserPublicInfoRepoResponse
    object UserNotFound : UserPublicInfoRepoResponse
}

data class CreateUserRepoRequest(
    val login: String,
    val passwordHash: String,
    val name: String,
    val roles: Set<String>,
)

interface UsersRepo {
    suspend fun getUserId(userLogin: UserLogin): UserIdRepoResponse
    suspend fun getUserAuthInfo(userId: UserId): UserAuthInfoRepoResponse
    suspend fun getUserPublicInfo(userId: UserId): UserPublicInfoRepoResponse
    suspend fun createUser(newUser: CreateUserRepoRequest): UserId
}

internal object UsersRepoObject : UsersRepo by UsersRepoImpl(
    connectionFactory = r2dbcConnectionFactory
)
internal class UsersRepoImpl(
    private val connectionFactory: ConnectionFactory
) : UsersRepo {

    private val getUserIdQuery = """
        SELECT id 
        FROM users 
        WHERE login=$1
    """.trimIndent()
    override suspend fun getUserId(userLogin: UserLogin): UserIdRepoResponse = runCatching {
        connectionFactory.create().awaitFirst()
            .createStatement(getUserIdQuery)
            .bind("$1", userLogin.login)
            .execute().awaitFirst()
    }.getOrElse { throw DatabaseException.DriverException(it) }
        .map { t, _ -> UserIdRepoResponse.Success(UserId(
            t.get("id", Integer::class.java)!!.toInt()
        )) }.awaitFirstOrNull() ?: UserIdRepoResponse.UserNotFound

    private val getUserAuthQuery = """
        SELECT password_hash, roles
        FROM users 
        WHERE id=$1
    """.trimIndent()
    override suspend fun getUserAuthInfo(userId: UserId): UserAuthInfoRepoResponse = runCatching {
        connectionFactory.create().awaitFirst()
            .createStatement(getUserAuthQuery)
            .bind("$1", userId.id)
            .execute().awaitFirst()
    }.getOrElse { throw DatabaseException.DriverException(it) }
        .map { t, _ ->
            UserAuthInfoRepoResponse.Success(
                passwordHash = t.get("password_hash", String::class.java)!!,
                roles = t.get("roles", Array<String>::class.java)!!.toSet()
            )
        }.awaitFirstOrNull() ?: UserAuthInfoRepoResponse.UserNotFound

    private val getUserPublicInfoQuery = """
        SELECT id, name, roles 
        FROM users 
        WHERE id=$1
    """.trimIndent()
    override suspend fun getUserPublicInfo(userId: UserId): UserPublicInfoRepoResponse = runCatching {
        connectionFactory.create().awaitFirst()
            .createStatement(getUserPublicInfoQuery)
            .bind("$1", userId.id)
            .execute().awaitFirst()
    }.getOrElse { throw DatabaseException.DriverException(it) }
        .map { t, _ ->
            UserPublicInfoRepoResponse.Success(
                name = t.get("name", String::class.java)!!,
            )
        }.awaitFirstOrNull() ?: UserPublicInfoRepoResponse.UserNotFound

    private val createUserQuery = """
        INSERT INTO users(login, password_hash, name, roles)
        VALUES ($1, $2, $3, $4)
        RETURNING id
    """.trimIndent()
    override suspend fun createUser(newUser: CreateUserRepoRequest): UserId = runCatching {
        connectionFactory.create().awaitFirst()
            .createStatement(createUserQuery)
            .bind("$1", newUser.login)
            .bind("$2", newUser.passwordHash)
            .bind("$3", newUser.name)
            .bind("$4", newUser.roles.toTypedArray())
            .execute().awaitFirst()
    }.getOrElse { throw DatabaseException.DriverException(it) }
        .map { t, _ ->
            UserId(t.get("id", Integer::class.java)!!.toInt())
        }.awaitFirst()
}
