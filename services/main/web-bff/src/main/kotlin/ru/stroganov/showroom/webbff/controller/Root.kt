package ru.stroganov.showroom.webbff.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Root {

    @GetMapping
    fun getRoot() {

    }
}