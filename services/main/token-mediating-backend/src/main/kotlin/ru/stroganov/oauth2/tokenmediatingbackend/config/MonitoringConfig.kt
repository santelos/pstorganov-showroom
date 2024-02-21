package ru.stroganov.oauth2.tokenmediatingbackend.config

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.registry.otlp.OtlpMeterRegistry

fun Application.monitoringConfigModule() {
//    Metrics.globalRegistry.add()
//    ClassLoaderMetrics().bindTo(Metrics.globalRegistry)
    install(MicrometerMetrics) {
        registry = Metrics.globalRegistry
    }
}
