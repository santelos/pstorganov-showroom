package ru.stroganov.oauth2.resourceserver.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController("messages")
class Controller {
    @GetMapping
    fun getMessages(): Array<String> {
        return arrayOf("Message 1", "Message 2", "Message 3")
    }
}