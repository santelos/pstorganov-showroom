package ru.stroganov.account.userauthservice.web.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Application.metricsConfig() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
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
