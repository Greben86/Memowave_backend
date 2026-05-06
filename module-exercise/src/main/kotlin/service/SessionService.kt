package dev.greben.memowave.service

import dev.greben.memowave.client.SessionClient
import dev.greben.memowave.utils.Constants
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

/**
 * Сервис управления сессиями
 */
@Service
class SessionService (
    private val sessionClient: SessionClient
) {

    /**
     * Проверка, что сессия активна
     *
     * @return true если сессия активна, иначе false
     */
    fun isAllowedSessionById(id: Long, token: String): Boolean =
        sessionClient.getSessionById(id, Constants.AUTH_BEARER_PREFIX + token).statusCode.is2xxSuccessful
}