package dev.greben.memowave.rest

import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.service.ImportService
import dev.greben.memowave.service.WordService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/words")
@Tag(name = "REST API: Слова")
class WordController(
    private val serviceWord: WordService,
    private val importService: ImportService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Получить все слова")
    @GetMapping(value = [""])
    @ResponseStatus(HttpStatus.OK)
    fun getAllWords(): List<WordResponse> {
        log.info { "Все слова" }
        return serviceWord.getAllWords()
    }

    @Operation(summary = "Выполнить импорт слов из файла")
    @GetMapping(value = ["/import"])
    @ResponseStatus(HttpStatus.OK)
    fun import(@RequestAttribute(required = true) fileName: String) {
        log.info { "Импорт слов из файла" }
        importService.import(fileName)
    }
}