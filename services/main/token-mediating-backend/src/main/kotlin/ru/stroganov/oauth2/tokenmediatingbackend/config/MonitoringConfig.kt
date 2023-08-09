package ru.stroganov.oauth2.tokenmediatingbackend.config

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.registry.otlp.OtlpMeterRegistry

fun Application.monitoringConfigModule() {
    val otlpRegistry = OtlpMeterRegistry()
    install(MicrometerMetrics) {
        registry = otlpRegistry
    }
}
