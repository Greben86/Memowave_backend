package dev.greben.memowave.util

import org.springframework.security.core.context.SecurityContextHolder

/**
 * Получение идентификатора текущего пользователя из JWT токена
 */
fun getCurrentUserId(): Long {
    val authentication = SecurityContextHolder.getContext().authentication
    val principal = authentication.principal
    
    // Предполагаем, что в principal находится идентификатор пользователя
    return when (principal) {
        is Long -> principal
        is String -> principal.toLongOrNull() ?: throw IllegalStateException("Invalid user ID format in token $principal")
        else -> throw IllegalStateException("User ID not found in token")
    }
}

/**
 * Проверка, является ли текущий пользователь администратором
 */
fun isCurrentUserAdmin(): Boolean {
    val authentication = SecurityContextHolder.getContext().authentication
    return authentication.authorities.any { it.authority == "ROLE_ADMIN" }
}