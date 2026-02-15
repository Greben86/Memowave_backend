package dev.greben.memowave.rest

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.service.CategoryService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/categories")
@Tag(name = "REST API: Категории")
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
    fun getAllCategories(): List<CategoryResponse>? {
        log.info { "Все категории слов" }
        return categoryService.getAllCategories()
    }

    @Operation(summary = "Добавить новую категорию слов")
    @PostMapping(value = ["category/new"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun newCategory(request: CategoryRequest?): CategoryResponse? {
        log.info { "Новая категория слов $request" }
        return categoryService.saveCategory(request)
    }

    @Operation(summary = "Обновить категорию слов")
    @PutMapping(value = ["category/{categoryId}/update"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun updateCategory(
        @PathVariable("categoryId") categoryId: Long,
        request: CategoryRequest?
    ): CategoryResponse? {
        log.info { "Обновление категории слов categoryId=$categoryId : $request" }
        return categoryService.updateCategory(categoryId, request)
    }

    @Operation(summary = "Удалить категорию слов")
    @DeleteMapping(value = ["category/{categoryId}/delete"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCategory(
        @PathVariable("categoryId") categoryId: Long
    ) {
        log.info { "Удаление категории слов categoryId=$categoryId" }
        categoryService.deleteCategory(categoryId)
    }
}