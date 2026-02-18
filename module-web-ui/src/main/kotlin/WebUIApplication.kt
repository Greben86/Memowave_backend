package dev.greben.memowave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebUIApplication

fun main(args: Array<String>) {
    runApplication<WebUIApplication>(*args)
}