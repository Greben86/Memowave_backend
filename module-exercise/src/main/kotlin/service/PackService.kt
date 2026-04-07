package dev.greben.memowave.service

import dev.greben.memowave.entities.Pack
import dev.greben.memowave.repository.PackRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис управления пакетами
 */
@Service
@Transactional
class PackService(
    private val repository: PackRepository
) {

    fun lookingForName(name: String): Pack? = repository.findFirstByName(name)
}