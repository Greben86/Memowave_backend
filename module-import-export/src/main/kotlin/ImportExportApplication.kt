package dev.greben.memowave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class ImportExportApplication

fun main(args: Array<String>) {
    runApplication<ImportExportApplication>(*args)
}