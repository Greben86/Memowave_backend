package dev.greben.memowave.rest

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.service.WordService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/words")
@Tag(name = "REST API: Слова")
class WordController(
    private val serviceWord: WordService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Получить все слова")
    @GetMapping(value = [""])
    @ResponseStatus(HttpStatus.OK)
    fun getAllWords(): List<WordResponse>? {
        log.info { "Все слова" }
        return serviceWord.getAllWords()
    }

    @Operation(summary = "Получить все слова категории")
    @GetMapping(value = ["{categoryId}/category"])
    @ResponseStatus(HttpStatus.OK)
    fun getWordsByCategory(@PathVariable("categoryId") categoryId: Long): List<WordResponse>? {
        log.info { "Все слова категории" }
        return serviceWord.getWordsByCategory(categoryId)
    }

    @Operation(summary = "Добавить новое слово")
    @PostMapping(value = ["word/new"])
    @ResponseStatus(HttpStatus.CREATED)
    fun newWord(request: WordRequest?): WordResponse? {
        log.info { "Новое слово $request" }
        return serviceWord.saveWord(request)
    }
}