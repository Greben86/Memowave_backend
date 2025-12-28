package dev.greben.memowave.rest

import dev.greben.memowave.dto.JwtAuthenticationResponse
import dev.greben.memowave.dto.SignInRequest
import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.service.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@RequestMapping("api/auth/sign")
@Tag(name = "REST API: Аутентификация")
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @Operation(summary = "Регистрация пользователя")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
        value = ["/up"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun signUp(@RequestBody @Valid request: SignUpRequest): JwtAuthenticationResponse {
        return authenticationService.signUp(request)
    }

    @Operation(summary = "Авторизация пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
        value = ["/in"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun signIn(@RequestBody @Valid request: SignInRequest): JwtAuthenticationResponse {
        return authenticationService.signIn(request)
    }
}