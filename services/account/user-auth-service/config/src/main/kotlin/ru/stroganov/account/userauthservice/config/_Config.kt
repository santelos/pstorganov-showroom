package ru.stroganov.account.userauthservice.config

val appConfig : AppConfig by lazy {
    AppConfig(
        userInfoService = AppConfig.UserInfoService(
            url = System.getenv("USER_INFO_SERVICE__URL")
        ),
        oauth2Client = AppConfig.Oauth2Client(
            clientId = System.getenv("OAUTH2__MAIN__CLIENT_ID"),
            clientSecret = System.getenv("OAUTH2__MAIN__CLIENT_SECRET"),
            publicUrl = System.getenv("OAUTH2__PUBLIC_URL"),
            scopes = listOf("user:CreateUser", "user:GetAuthInfo")
        ),
        hydra = AppConfig.HydraClient(
            adminUrl = System.getenv("OAUTH2__ADMIN_URL"),
            defaultHost = "santelos.com"
        ),
        monitoring = AppConfig.MonitoringConfig(
            applicationName = "user-auth-service"
        )
    )
}
