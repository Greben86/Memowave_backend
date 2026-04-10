package dev.greben.memowave.service

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.entities.Category
import dev.greben.memowave.mapper.WordMapper
import dev.greben.memowave.repository.WordRepository
import dev.greben.memowave.util.getCurrentUserId
import dev.greben.memowave.util.isCurrentUserAdmin
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

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
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Выборка всех слов
     *
     * @return список слов
     */
    fun getAllWords(): List<WordResponse> {
        val isAdmin = isCurrentUserAdmin()

        val allWords = when(isAdmin){
            true -> repository.findAll()
            false -> repository.findAllByUserId(getCurrentUserId())
        }

        return allWords.stream()
            .map { mapper.toDto(it) }
            .toList()
    }

    /**
     * Выборка слова по Id
     *
     * @return список слов
     */
    fun getWordById(wordId: Long): WordResponse? =
        repository.findById(wordId)
            .map { mapper.toDto(it) }
            .getOrNull()

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

        var category: Category? = null
        if (request.categoryId != null) {
            category = categoryService.getById(request.categoryId!!)
            if (category == null) {
                log.warn { "!! Category with id ${request.categoryId} not found" }
            }
        }

        val currentUserId = getCurrentUserId()
        var entity = mapper.fromDto(request)
        entity.category = category
        entity.userId = currentUserId
        entity = repository.save(entity)

        return mapper.toDto(entity)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveWords(requests: List<WordRequest>): List<WordResponse> {
        if (requests.isEmpty()) {
            log.warn { "!! Request list is empty" }
            return listOf()
        }

        val currentUserId = getCurrentUserId()
        val results = ArrayList<WordResponse>()
        requests.forEach {
            var category: Category? = null
            if (it.categoryId != null) {
                category = categoryService.getById(it.categoryId!!)
                if (category == null) {
                    log.warn { "!! Category with id ${it.categoryId} not found" }
                }
            }

            var entity = mapper.fromDto(it)
            entity.category = category
            entity.userId = currentUserId
            entity = repository.save(entity)
            results.add(mapper.toDto(entity))
        }

        return results
    }

    fun updateWord(wordId: Long, request: WordRequest?): WordResponse? {
        if (request == null) {
            log.warn { "!! Request can not be null" }
            return null
        }

        var entity = repository.findById(wordId)
            .orElseThrow { IllegalArgumentException("!! Word with id=$wordId not found") }

        entity = mapper.updateFromDto(entity, request)
        entity = repository.save(entity)

        return mapper.toDto(entity)
    }

    fun deleteWord(wordId: Long) {
        val entity = repository.findById(wordId)
            .orElseThrow { IllegalArgumentException("!! Word with id=$wordId not found") }

        repository.delete(entity)
    }
}