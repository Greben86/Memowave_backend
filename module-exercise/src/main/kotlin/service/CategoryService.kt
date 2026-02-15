package dev.greben.memowave.service

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.entities.Category
import dev.greben.memowave.mapper.CategoryMapper
import dev.greben.memowave.repository.CategoryRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

/**
 * Сервис управления категориями
 */
@Service
@Transactional
class CategoryService(
    private val packService: PackService,
    private val repository: CategoryRepository,
    private val mapper: CategoryMapper
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Выборка всех категорий слов
     *
     * @return список категорий
     */
    fun getById(id: Long): Category? =
        repository.findById(id)
            .getOrNull()

    /**
     * Выборка всех категорий слов
     *
     * @return список категорий
     */
    fun getAllCategories(): List<CategoryResponse> =
        repository.findAll().stream()
            .map { mapper.toDto(it) }
            .toList()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveCategory(request: CategoryRequest?): CategoryResponse? {
        if (request == null) {
            log.warn { "!! Request can not be null" }
            return null
        }

        val pack = packService.lookingForName("ENG")
        if (pack == null) {
            log.warn { "!! Pack with name ENG not found" }
            return null
        }

        var entity = mapper.fromDto(request)
        entity.pack = pack
        entity = repository.save(entity)

        return mapper.toDto(entity)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateCategory(categoryId: Long, request: CategoryRequest?): CategoryResponse? {
        if (request == null) {
            log.warn { "!! Request can not be null" }
            return null
        }

        var entity = repository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("!! Category with id=$categoryId not found") }

        entity = mapper.updateFromDto(entity, request)
        entity = repository.save(entity)

        return mapper.toDto(entity)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun deleteCategory(categoryId: Long) {
        val entity = repository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("!! Category with id=$categoryId not found") }

        repository.delete(entity)
    }

    fun lookingForName(name: String): Category? = repository.findByName(name)
}