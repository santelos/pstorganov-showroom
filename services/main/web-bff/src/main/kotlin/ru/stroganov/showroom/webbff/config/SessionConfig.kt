package ru.stroganov.showroom.webbff.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession


@EnableRedisWebSession
//@DependsOn("redisConnectionFactory")
class SessionConfig {

//    @Bean
//    fun redisConnectionFactory(): LettuceConnectionFactory = LettuceConnectionFactory()
}
