package ru.stroganov.showroom.account.userinfoservice

val appConfig = AppConfig(
    oauth2 = AppConfig.Oauth2Config(
        adminUri = System.getenv("OAUTH2__ADMIN_URI")
    ),
    database = AppConfig.DatabaseConfig(
        host = System.getenv("DATABASE__HOST"),
        port = System.getenv("DATABASE__PORT") ?: "5432",
        username = System.getenv("DATABASE__USERNAME"),
        password = System.getenv("DATABASE__PASSWORD"),
        database = System.getenv("DATABASE__DATABASE_NAME"),
    )
)
data class AppConfig(
    val oauth2: Oauth2Config,
    val database: DatabaseConfig
) {
    data class Oauth2Config(
        val adminUri: String
    )
    data class DatabaseConfig(
        val host: String,
        val port: String,
        val username: String,
        val password: String,
        val database: String
    )
}
