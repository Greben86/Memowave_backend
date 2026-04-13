package dev.greben.memowave.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
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
 * Контроллер для перехвата исключений
 */
@RestControllerAdvice(basePackageClasses = [UserController::class])
class ErrorAdvice {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Если поймали исключение валидации [org.springframework.web.bind.MethodArgumentNotValidException], то возвращаем статус 400
     *
     * @return ответ со статусом 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): Map<String?, List<String?>> {
        log.error { "Ошибка валидации: ${ex.message}" }
        val fieldErrors = ex.fieldErrors.stream()
            .filter { it.defaultMessage != null }
            .map { Pair.of(it.field, it.defaultMessage!!) }
        val globalErrors = ex.globalErrors.stream()
            .filter { it.defaultMessage != null }
            .map { Pair.of(it.objectName, it.defaultMessage!!) }

        return Stream.concat(fieldErrors, globalErrors)
            .collect(
                Collectors.groupingBy(
                    Function { it.getFirst() },
                    Collectors
                        .mapping(Function { it.getSecond() }, Collectors.toList())
                )
            )
    }

    /**
     * Если поймали исключение [SecurityException], то возвращаем статус 403
     *
     * @return ответ со статусом 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SecurityException::class)
    fun handleSecurityErrors(ex: SecurityException): String? {
        log.error { "Ошибка безопасности: ${ex.message}" }
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
        log.error { "Ошибка JWT токена: ${ex.message}" }
        return "Ошибка JWT токена: ${ex.message}"
    }

    /**
     * Если поймали любое исключение [ExpiredJwtException], то возвращаем статус 500
     *
     * @return ответ со статусом 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleAnyErrors(ex: Exception): String? {
        log.error { "Ошибка работы сервера ${ex.javaClass.simpleName}: ${ex.message}" }
        return "${ex.javaClass.simpleName}: ${ex.message}"
    }
}