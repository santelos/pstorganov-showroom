package ru.stroganov.oauth2.resourceserver.controller

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/")
class Controller {

    @GetMapping
    fun getMessages(authentication: Authentication): Array<String> {
        return arrayOf("Message 1", "Message 2", "Message 3")
    }
}