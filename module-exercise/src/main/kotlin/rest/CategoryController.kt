package dev.greben.memowave.rest

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.service.CategoryService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/categories")
@Tag(name = "REST API: Категории")
@SecurityRequirement(name = "jwt-token")
class CategoryController(
    private val categoryService: CategoryService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Получить все категории слов")
    @GetMapping(value = [""],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAllCategories(): List<CategoryResponse> {
        log.info { "Все категории слов" }
        return categoryService.getAllCategories()
    }

    @Operation(summary = "Получить категорию слов по идентификатору")
    @GetMapping(value = ["{categoryId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getCategoryById(@PathVariable("categoryId") categoryId: Long): CategoryResponse? {
        log.info { "Категория слов по идентификатору id=$categoryId" }
        return categoryService.getCategoryById(categoryId)
    }

    @Operation(summary = "Добавить новую категорию слов")
    @PostMapping(value = [""],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun newCategory(@RequestBody @Valid request: CategoryRequest?): CategoryResponse? {
        log.info { "Новая категория слов $request" }
        return categoryService.saveCategory(request)
    }

    @Operation(summary = "Обновить категорию слов")
    @PutMapping(value = ["{categoryId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun updateCategory(
        @PathVariable("categoryId") categoryId: Long,
        @RequestBody @Valid request: CategoryRequest?
    ): CategoryResponse? {
        log.info { "Обновление категории слов categoryId=$categoryId : $request" }
        return categoryService.updateCategory(categoryId, request)
    }

    @Operation(summary = "Удалить категорию слов")
    @DeleteMapping(value = ["{categoryId}"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCategory(
        @PathVariable("categoryId") categoryId: Long
    ) {
        log.info { "Удаление категории слов categoryId=$categoryId" }
        categoryService.deleteCategory(categoryId)
    }

    @Operation(summary = "Копировать категорию для пользователя")
    @PostMapping(value = ["{categoryId}/copy"],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun copyCategoryForUser(
        @PathVariable("categoryId") categoryId: Long
    ): CategoryResponse? {
        log.info { "Копирование категории categoryId=$categoryId для текущего пользователя" }
        return categoryService.copyCategoryForUser(categoryId)
    }
}