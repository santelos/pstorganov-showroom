package ru.stroganov.account.userauthservicek.repo.config

import ru.stroganov.account.userauthservicek.config.AppConfig
import sh.ory.hydra.ApiClient
import sh.ory.hydra.api.AdminApi

internal fun hydraClient(
    config: AppConfig.HydraClient
): AdminApi = AdminApi(ApiClient().apply {
    basePath = config.adminUrl
    addDefaultHeader("Host", config.defaultHost)
})
