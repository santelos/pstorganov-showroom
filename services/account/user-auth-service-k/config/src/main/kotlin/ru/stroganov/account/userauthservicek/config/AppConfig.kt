package ru.stroganov.account.userauthservicek.config

data class AppConfig(
    val database: Database,
    val userInfoService: UserInfoService,
    val oauth2Client: Oauth2Client,
    val hydra: HydraClient
) {
    data class Database(
        val host: String,
        val port: String,
        val database: String,
        val user: String,
        val password: String
    )
    data class UserInfoService(
        val host: String
    )
    data class Oauth2Client(
        val clientId: String,
        val clientSecret: String,
        val publicUrl: String,
        val scopes: List<String>
    )
    data class HydraClient(
        val adminUrl: String,
        val defaultHost: String
    )
}
