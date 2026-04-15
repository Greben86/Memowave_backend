package dev.greben.memowave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer // включает функциональность сервера конфигураций
class ConfigurationApplication

fun main(args: Array<String>) {
    runApplication<ConfigurationApplication>(*args)
}