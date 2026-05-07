package dev.greben.memowave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer // Включаем режим Eureka Server
class DiscoveryApplication

fun main(args: Array<String>) {
    runApplication<DiscoveryApplication>(*args)
}