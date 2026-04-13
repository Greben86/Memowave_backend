package dev.greben.memowave.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.data.util.Pair
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Контроллер для перехвата исключений авторизации
 */
@RestControllerAdvice(basePackageClasses = [AuthController::class])
class AuthErrorAdvice {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Если поймали ошибку авторизации, то возвращаем статус 401
     *
     * @return ответ со статусом 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(Exception::class)
    fun handleAuthErrors(ex: Exception): String? {
        log.error { "Ошибка авторизации ${ex.javaClass.simpleName}: ${ex.message}" }
        return "Ошибка авторизации ${ex.javaClass.simpleName}: ${ex.message}"
    }
}