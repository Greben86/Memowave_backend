package dev.greben.memowave.rest

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.service.WordService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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
    @GetMapping(value = [""],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAllWords(): List<WordResponse> {
        log.info { "Все слова" }
        return serviceWord.getAllWords()
    }

    @Operation(summary = "Получить слово по идентификатору")
    @GetMapping(value = ["word/{wordId}/get"],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getWordById(@PathVariable("wordId") wordId: Long): WordResponse? {
        log.info { "Слово по идентификатору id=$wordId" }
        return serviceWord.getWordById(wordId)
    }

    @Operation(summary = "Получить все слова категории")
    @GetMapping(value = ["{categoryId}/category"],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getWordsByCategory(@PathVariable("categoryId") categoryId: Long): List<WordResponse> {
        log.info { "Все слова категории $categoryId" }
        return serviceWord.getWordsByCategory(categoryId)
    }

    @Operation(summary = "Добавить новое слово")
    @PostMapping(value = ["word/add"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun addWord(@RequestBody @Valid request: WordRequest?): WordResponse? {
        log.info { "Новое слово $request" }
        return serviceWord.saveWord(request)
    }

    @Operation(summary = "Добавить новые слова в категорию")
    @PostMapping(value = ["{categoryId}/add/all"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun addWords(
        @PathVariable("categoryId") categoryId: Long,
        @RequestBody @Valid request: List<WordRequest>): List<WordResponse> {
        log.info { "Новые слова $request в категорию $categoryId" }
        return serviceWord.saveWords(request, categoryId)
    }

    @Operation(summary = "Обновить слово")
    @PutMapping(value = ["word/{wordId}/update"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun updateWord(
        @PathVariable("wordId") wordId: Long,
        @RequestBody @Valid request: WordRequest?
    ): WordResponse? {
        log.info { "Обновление слова wordId=$wordId : $request" }
        return serviceWord.updateWord(wordId, request)
    }

    @Operation(summary = "Удалить слово")
    @PutMapping(value = ["word/{wordId}/delete"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteWord(
        @PathVariable("wordId") wordId: Long
    ) {
        log.info { "Удаление слова wordId=$wordId" }
        return serviceWord.deleteWord(wordId)
    }
}