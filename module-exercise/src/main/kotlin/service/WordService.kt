package dev.greben.memowave.service

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.mapper.WordMapper
import dev.greben.memowave.repository.WordRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
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
     * Проверка, является ли текущий пользователь администратором
     */
    private fun isCurrentUserAdmin(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    /**
     * Получение идентификатора текущего пользователя из JWT токена
     */
    private fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        authentication.details

        // Предполагаем, что в principal находится идентификатор пользователя
        return when (principal) {
            is Long -> principal
            is String -> principal.toLongOrNull() ?: throw IllegalStateException("Invalid user ID format in token $principal")
            else -> throw IllegalStateException("User ID not found in token")
        }
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