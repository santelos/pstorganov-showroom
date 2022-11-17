package ru.stroganov.showroom.account.userinfoservice.repo

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.stroganov.showroom.account.userinfoservice.service.UserId

@Testcontainers
internal class UsersRepoImplTest {

    @Container
    private val postgresContainer = PostgreSQLContainer("postgres:15")
        .withUsername("postgres")
        .withPassword("postgres")
        .withDatabaseName("postgres")

    private data class ConnectionAndRepo(
        val repo: UsersRepo,
        val connectionFactory: ConnectionFactory,
    )

    @BeforeEach
    fun setUp() {
        val host = postgresContainer.host
        val port = postgresContainer.getMappedPort(5432)
        val databaseName = postgresContainer.databaseName
        Flyway.configure()
            .dataSource(
                "jdbc:postgresql://$host:$port/$databaseName",
                postgresContainer.username,
                postgresContainer.password
            ).load()
            .migrate()
    }

    private fun createTestable(): ConnectionAndRepo {
        val host = postgresContainer.host
        val port = postgresContainer.getMappedPort(5432)
        val databaseName = postgresContainer.databaseName
        val connectionFactory: ConnectionFactory = ConnectionFactories
            .get(
                ConnectionFactoryOptions
                    .parse("r2dbc:postgresql://$host:$port/$databaseName")
                    .mutate()
                    .option(ConnectionFactoryOptions.USER, postgresContainer.username)
                    .option(ConnectionFactoryOptions.PASSWORD, postgresContainer.password)
                    .build()
            )
        return ConnectionAndRepo(UsersRepoImpl(connectionFactory), connectionFactory)
    }

    @Test
    fun getUserInfo() = runBlocking {
        val (usersRepo, connectionFactory) = createTestable()
        val name = "test--name"
        val userId = connectionFactory.createUser(login = name)
        val expected = UserInfoRepoResponse(userId, name)
        val actual = usersRepo.getUserInfo(UserId(userId))
        assertEquals(expected, actual)
    }

    @Test
    fun getUserCredentials() = runBlocking {
        val (usersRepo, connectionFactory) = createTestable()
        val expected = UserCredentialsRepoResponse("test--login", "test--password-hash")
        val userId = connectionFactory.createUser(login = expected.login, passwordHash = expected.passwordHash)
        val actual = usersRepo.getUserCredentials(UserId(userId))
        assertEquals(expected, actual)
    }

    @Test
    fun createUser() {
        runBlocking {
            val (usersRepo, connectionFactory) = createTestable()
            val createUserRepoRequest = CreateUserRepoRequest(
                login = "test--login",
                passwordHash = "test--password-hash",
                name = "test--name",
            )
            val userId = usersRepo.createUser(createUserRepoRequest)
            connectionFactory.create().awaitFirst()
                .createStatement("SELECT login, password_hash, name FROM users WHERE id=$1")
                .bind("$1", userId.id).execute().awaitFirst()
                .map { t, _ ->
                    assertEquals(t.get("login", String::class.java), createUserRepoRequest.login)
                    assertEquals(t.get("password_hash", String::class.java), createUserRepoRequest.passwordHash)
                    assertEquals(t.get("name", String::class.java), createUserRepoRequest.name)
                }.awaitFirst()
        }
    }

    private suspend fun ConnectionFactory.createUser(
        login: String = "test--login",
        passwordHash: String = "test--password-hash",
        name: String = "test--name"
    ): Int {
        val query = """
            INSERT INTO users(login, password_hash, name)
            VALUES ($1, $2, $3)
            RETURNING id
        """.trimIndent()
        val result = create().awaitFirst()
            .createStatement(query)
            .bind("$1", login)
            .bind("$2", passwordHash)
            .bind("$3", name)
            .execute().awaitFirst()
        return result
            .map { t, _ -> t.get("id", Integer::class.java)!!.toInt() }
            .awaitFirst()
    }
}
