package dev.greben.memowave.rest

import dev.greben.memowave.dto.JwtAuthenticationResponse
import dev.greben.memowave.dto.SignInRequest
import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.service.AuthenticationService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/auth")
@Tag(name = "REST API: Аутентификация")
class AuthController(
    private val authenticationService: AuthenticationService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Регистрация пользователя")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
        value = ["/register"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun signUp(@RequestBody @Valid request: SignUpRequest): JwtAuthenticationResponse {
        log.info { "Регистрация пользователя" }
        return authenticationService.signUp(request)
    }

    @Operation(summary = "Авторизация пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
        value = ["/login"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun signIn(@RequestBody @Valid request: SignInRequest): JwtAuthenticationResponse {
        log.info { "Авторизация пользователя" }
        return authenticationService.signIn(request)
    }
}