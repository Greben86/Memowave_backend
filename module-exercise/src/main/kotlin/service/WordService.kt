package dev.greben.memowave.service

import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.mapper.WordMapper
import dev.greben.memowave.repository.WordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис управления словами
 */
@Service
@Transactional
class WordService(
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

}