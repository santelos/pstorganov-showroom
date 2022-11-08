package ru.stroganov.showroom.webbff.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.adapter.ForwardedHeaderTransformer

@Configuration
class ForwardConfig {

    @Bean
    fun forwardedHeaderTransformer(): ForwardedHeaderTransformer = ForwardedHeaderTransformer()
}