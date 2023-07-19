package ru.stroganov.account.userauthservice.repo.config

import ru.stroganov.account.userauthservice.config.AppConfig
import sh.ory.hydra.ApiClient
import sh.ory.hydra.api.AdminApi

internal fun hydraClient(
    config: AppConfig.HydraClient
): AdminApi = AdminApi(
    ApiClient().apply {
        basePath = config.adminUrl
        addDefaultHeader("Host", config.defaultHost)
    }
)
