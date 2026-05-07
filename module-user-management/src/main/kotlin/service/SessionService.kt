package dev.greben.memowave.service

import dev.greben.memowave.dto.SessionResponse
import dev.greben.memowave.entities.Session
import dev.greben.memowave.entities.User
import dev.greben.memowave.mapper.SessionMapper
import dev.greben.memowave.repository.SessionRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Сервис управления сессиями
 */
@Service
@Transactional
class SessionService (
    private val userService: UserService,
    private val repository: SessionRepository,
    private val mapper: SessionMapper,

    // Время жизни refresh токена в миллисекундах
    @Value("\${security.token.expiration.refresh.minutes}")
    val jwtRefreshExpirationMinutes: Int
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Выборка всех активных сессий
     *
     * @return список активных сессий пользователя
     */
    fun getAllSessions(): List<SessionResponse> {
        val user = userService.getCurrentUser()!!
        return repository.findActiveSessionsByUserId(user.id!!,
            LocalDateTime.now().minusMinutes(jwtRefreshExpirationMinutes.toLong()))
            .stream()
            .map { mapper.toDto(it) }
            .toList()
    }

    /**
     * Выборка всех активных сессий
     *
     * @return список активных сессий пользователя
     */
    fun getSessionById(id: Long): SessionResponse? {
        return repository.findActiveSessionById(id,
            LocalDateTime.now().minusMinutes(jwtRefreshExpirationMinutes.toLong()))
            .map session@{
                log.info { "Сессия $id найдена" }
                return@session mapper.toDto(it)
            }
            .orElseGet nosession@{
                log.info { "Сессия $id не найдена" }
                return@nosession null
            }
    }

    /**
     * Проверка, что сессия активна
     *
     * @return true если сессия активна, иначе false
     */
    fun isAllowedSessionById(id: Long): Boolean {
        return repository.findActiveSessionById(id,
            LocalDateTime.now().minusMinutes(jwtRefreshExpirationMinutes.toLong()))
            .map session@{
                log.info { "Активная сессия $id найдена" }
                return@session true
            }
            .orElseGet nosession@{
                log.info { "Активная сессия $id не найдена" }
                return@nosession false
            }
    }

    /**
     * Выборка всех активных сессий
     *
     * @return список активных сессий пользователя
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun createSessionByName(name: String, user: User): Session {
        val entity = repository.findActiveSessionByName(name, user.id!!,
            LocalDateTime.now().minusMinutes(jwtRefreshExpirationMinutes.toLong()))
            .orElseGet { Session(name = name, userId = user.id, isDenied = false) }
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity)
    }

    /**
     * Отключение сессии
     *
     * @param id идентификатор сессии
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun setDenied(id: Long) {
        val session = repository.findById(id)
            .orElseThrow { IllegalArgumentException("Сессия $id не найдена") }
        session.isDenied = true
        repository.save(session)
    }
}