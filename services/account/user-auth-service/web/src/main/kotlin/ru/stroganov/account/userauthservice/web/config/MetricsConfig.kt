package ru.stroganov.account.userauthservice.web.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import ru.stroganov.account.userauthservice.config.AppConfig
import ru.stroganov.account.userauthservice.config.appConfig

fun Application.metricsConfig(
    config: AppConfig.MonitoringConfig = appConfig.monitoring
) {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT).apply {
        config().commonTags("application", config.applicationName)
    }
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
    routing {
//        authenticate(OAUTH2_AUTH) {
//            roleAuthorize(RoleRule.HasRoles(setOf("metrics:Get")), ::principalToRolesMapping) {
//
//            }
//        }
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
