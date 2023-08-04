package ru.stroganov.oauth2.tokenmediatingbackend.routing

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import ru.stroganov.oauth2.tokenmediatingbackend.AppConfig
import ru.stroganov.oauth2.tokenmediatingbackend.appConfig

fun Application.monitoringRoutingModule(
    config: AppConfig.MonitoringConfig = appConfig.monitoringConfig
) {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT).apply {
        config().commonTags("application", config.applicationName)
    }
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
    routing {
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
