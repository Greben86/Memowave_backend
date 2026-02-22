package dev.greben.memowave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
class ExerciseApplication

fun main(args: Array<String>) {
    runApplication<ExerciseApplication>(*args)
}