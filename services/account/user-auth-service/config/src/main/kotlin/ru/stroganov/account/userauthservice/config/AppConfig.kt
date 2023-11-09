package ru.stroganov.account.userauthservice.config

data class AppConfig(
    val userInfoService: UserInfoService,
    val oauth2Client: Oauth2Client,
    val hydra: HydraClient,
    val monitoring: MonitoringConfig
) {
    data class UserInfoService(
        val url: String
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
    data class MonitoringConfig(
        val applicationName: String
    )
}
