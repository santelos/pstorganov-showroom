package ru.stroganov.showroom.webbff.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.config.web.server.invoke

@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        authorizeExchange {
            authorize(anyExchange, permitAll)
        }
        oauth2Client {  }
    }
}
