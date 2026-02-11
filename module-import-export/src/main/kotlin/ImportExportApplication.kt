package dev.greben.memowave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ImportExportApplication

fun main(args: Array<String>) {
    runApplication<ImportExportApplication>(*args)
}