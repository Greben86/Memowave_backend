package dev.greben.memowave.service

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.entities.Word
import dev.greben.memowave.mapper.WordMapper
import dev.greben.memowave.repository.WordRepository
import dev.greben.memowave.service.ImportService.Companion.log
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис управления словами
 */
@Service
@Transactional
class WordService(
    private val categoryService: CategoryService,
    private val repository: WordRepository,
    private val mapper: WordMapper
) {

    /**
     * Выборка всех слов
     *
     * @return список слов
     */
    fun getAllWords(): List<WordResponse> {
        return repository.findAll().stream()
            .map { mapper.toDto(it) }
            .toList()
    }

    /**
     * Выборка всех слов категории
     *
     * @return список слов категории
     */
    fun getWordsByCategory(categoryId: Long): List<WordResponse> {
        return repository.findByCategoryId(categoryId).stream()
            .map { mapper.toDto(it) }
            .toList()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveWord(request: WordRequest?): WordResponse? {
        if (request == null) {
            log.warn { "!! Request can not be null" }
            return null
        }

        val category = categoryService.getById(request.categoryId!!)
        if (category == null) {
            log.warn { "!! Category with id ${request.categoryId} not found" }
            return null
        }

        var entity = mapper.fromDto(request)
        entity.category = category
        entity = repository.save(entity)

        return mapper.toDto(entity)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveWords(requests: List<WordRequest>, categoryId: Long): List<WordResponse> {
        if (requests.isEmpty()) {
            log.warn { "!! Request list is empty" }
            return listOf()
        }

        val category = categoryService.getById(categoryId)
        if (category == null) {
            log.warn { "!! Category with id $categoryId not found" }
            return listOf()
        }

        var results = ArrayList<WordResponse>()
        requests.forEach {
            var entity = mapper.fromDto(it)
            entity.category = category
            entity = repository.save(entity)
            results.add(mapper.toDto(entity))
        }

        return results
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveWords(words: List<Word>) {
        words.forEach { repository.save(it) }
    }

}