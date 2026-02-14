package dev.greben.memowave.service

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.entities.Category
import dev.greben.memowave.mapper.CategoryMapper
import dev.greben.memowave.repository.CategoryRepository
import dev.greben.memowave.service.ImportService.Companion.log
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


    /**
     * Выборка всех категорий слов
     *
     * @return список категорий
     */
    fun getById(id: Long): Category? {
        return repository.findById(id)
            .getOrNull()
    }

    /**
     * Выборка всех категорий слов
     *
     * @return список категорий
     */
    fun getAllCategories(): List<CategoryResponse> {
        return repository.findAll().stream()
            .map { mapper.toDto(it) }
            .toList()
    }

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

    fun lookingForName(name: String): Category? = repository.findByName(name)
}