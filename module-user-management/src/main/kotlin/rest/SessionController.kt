package dev.greben.memowave.rest

import dev.greben.memowave.dto.SessionResponse
import dev.greben.memowave.service.SessionService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/sessions")
@Tag(name = "REST API: Активные сессии")
@SecurityRequirement(name = "jwt-token")
class SessionController(
    private val sessionService: SessionService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Получить все активные сессии пользователя")
    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAllSessions(): List<SessionResponse> {
        log.info { "Все активные сессии" }
        return sessionService.getAllSessions()
    }

    @Operation(summary = "Получить сессию по идентификатору")
    @GetMapping(value = ["/{sessionId}"])
    fun getSessionById(@PathVariable("sessionId") sessionId: Long): ResponseEntity<SessionResponse?> {
        log.info { "Получить сессию по идентификатору $sessionId" }
        val response = sessionService.getSessionById(sessionId)
        return if (response == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } else {
            ResponseEntity.ok().body(response)
        }
    }

    @PutMapping(value = ["/{sessionId}/set-denied"])
    @Operation(summary = "Выключить сессию")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun setDenied(@PathVariable("sessionId") sessionId: Long) {
        log.info { "Отключение сессии $sessionId" }
        sessionService.setDenied(sessionId)
    }
}