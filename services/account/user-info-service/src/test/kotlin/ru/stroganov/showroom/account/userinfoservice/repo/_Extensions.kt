package ru.stroganov.showroom.account.userinfoservice.repo

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer

internal data class ConnectionAndRepo(
    val repo: UsersRepo,
    val connectionFactory: ConnectionFactory,
)

internal fun createTestable(ds: PostgreSQLContainer<*>): ConnectionAndRepo {
    val host = ds.host
    val port = ds.getMappedPort(5432)
    val databaseName = ds.databaseName
    val connectionFactory: ConnectionFactory = ConnectionFactories
        .get(
            ConnectionFactoryOptions
                .parse("r2dbc:postgresql://$host:$port/$databaseName")
                .mutate()
                .option(ConnectionFactoryOptions.USER, ds.username)
                .option(ConnectionFactoryOptions.PASSWORD, ds.password)
                .build()
        )
    return ConnectionAndRepo(UsersRepoImpl(connectionFactory), connectionFactory)
}

internal fun migration(ds: PostgreSQLContainer<*>) {
    val host = ds.host
    val port = ds.getMappedPort(5432)
    val databaseName = ds.databaseName
    Flyway.configure()
        .dataSource(
            "jdbc:postgresql://$host:$port/$databaseName",
            ds.username,
            ds.password
        ).load()
        .migrate()
}

internal suspend fun ConnectionFactory.createUser(
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

internal suspend fun ConnectionFactory.truncate(name: String) {
    val query = """
        TRUNCATE $name
    """.trimIndent()
    val result = create().awaitFirst()
        .createStatement(query)
        .execute().awaitFirst()
    result.rowsUpdated.awaitFirstOrNull()
}
