package ru.stroganov.oauth2.tokenmediatingbackend

val appConfig = AppConfig(
    successRedirect = System.getenv("SUCCESS_REDIRECT"),
    oauth2Config = AppConfig.Oauth2Config(
        callbackUrl = System.getenv("OAUTH2__CALLBACK_URL"),
        providerUrl = System.getenv("OAUTH2__PROVIDER_URL"),
        providerInternalUrl = System.getenv("OAUTH2__PROVIDER_INTERNAL_URL"),
        clientId = System.getenv("OAUTH2__CLIENT_ID"),
        clientSecret = System.getenv("OAUTH2__CLIENT_SECRET")
    ),
    monitoringConfig = AppConfig.MonitoringConfig(
        applicationName = "token-mediating-backend"
    )
)

data class AppConfig(
    val successRedirect: String,
    val oauth2Config: Oauth2Config,
    val monitoringConfig: MonitoringConfig
) {
    data class Oauth2Config(
        val callbackUrl: String,
        val providerUrl: String,
        val providerInternalUrl: String,
        val clientId: String,
        val clientSecret: String
    )
    data class MonitoringConfig(
        val applicationName: String
    )
}
