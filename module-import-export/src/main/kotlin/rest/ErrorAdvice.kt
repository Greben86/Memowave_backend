package dev.greben.memowave.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import io.minio.errors.MinioException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Контроллер для перехвата исключений
 */
@RestControllerAdvice
class ErrorAdvice {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Если поймали исключение [SecurityException], то возвращаем статус 403
     *
     * @return ответ со статусом 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SecurityException::class)
    fun handleSecurityErrors(ex: SecurityException): String? {
        log.error(ex) { "Ошибка безопасности: ${ex.message}" }
        return "Ошибка безопасности: ${ex.message}"
    }

    /**
     * Если поймали ошибку авторизации, то возвращаем статус 401
     *
     * @return ответ со статусом 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException::class)
    fun handleJwtErrors(ex: JwtException): String? {
        log.error(ex) { "Ошибка JWT токена: ${ex.message}" }
        return "Ошибка JWT токена: ${ex.message}"
    }

    /**
     * Если поймали любое исключение [MinioException], то возвращаем статус 404
     *
     * @return ответ со статусом 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MinioException::class)
    fun handleMinioError(ex: MinioException): String? {
        log.error(ex) { "Ошибка Minio ${ex.javaClass.simpleName}: ${ex.message}" }
        return "Ошибка Minio ${ex.javaClass.simpleName}: ${ex.message}"
    }

    /**
     * Если поймали любое исключение [Exception], то возвращаем статус 500
     *
     * @return ответ со статусом 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleAnyErrors(ex: Exception): String? {
        log.error(ex) { "Ошибка работы сервера ${ex.javaClass.simpleName}: ${ex.message}" }
        return "Ошибка работы сервера ${ex.javaClass.simpleName}: ${ex.message}"
    }
}