package dev.greben.memowave

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableAutoConfiguration
class UserManagementApplication

fun main(args: Array<String>) {
    runApplication<UserManagementApplication>(*args)
}

@RestController
class MainController {

    @GetMapping("/hello")
    fun load(): String {
        return "Hello world"
    }
}