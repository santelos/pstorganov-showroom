package ru.stroganov.oauth2.resourceserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.mvcMatcher("/messages/**")
            .authorizeRequests()
            .mvcMatchers("/messages/**")
            .access("hasAuthority('SCOPE_resource.read')")
            .and()
            .oauth2ResourceServer()
            .jwt()
        return http.build()
    }
}