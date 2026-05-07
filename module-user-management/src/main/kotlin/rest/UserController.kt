package dev.greben.memowave.rest

import dev.greben.memowave.dto.ChangePasswordRequest
import dev.greben.memowave.dto.UserResponse
import dev.greben.memowave.service.AuthenticationService
import dev.greben.memowave.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/users")
@Tag(name = "REST API: Пользователь")
@SecurityRequirement(name = "jwt-token")
class UserController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Редактирование пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(
        value = ["/me"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun editUser(@RequestBody @Valid dto: UserResponse?): UserResponse? {
        log.info { "Редактирование пользователя" }
        return userService.updateCurrentUser(dto)
    }

    @Operation(
        summary = "Список всех пользователей, кроме администраторов",
        description = "Требуется роль ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN')")
    fun getUsers(): List<UserResponse> {
        log.info { "Список всех пользователей, кроме администраторов" }
        return userService.getAllUsers()
    }

    @Operation(
        summary = "Удаление пользователя",
        description = "Требуется роль ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = ["/{id}"])
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(@PathVariable("id") id: Long): ResponseEntity<Nothing> {
        log.info { "Удаление пользователя" }
        if (userService.getById(id) != null) {
            userService.deleteUser(id)
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }

    @PutMapping(value = ["/{id}/set-admin"])
    @Operation(
        summary = "Добавить роль ADMIN пользователю",
        description = "Требуется роль ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    fun setAdmin(@PathVariable("id") id: Long) {
        log.info { "Добавление роли ADMIN пользователю" }
        userService.setAdmin(id)
    }

    @PutMapping(value = ["/{id}/send-code"])
    @Operation(summary = "Отправка нового OTP пользователю")
    @ResponseStatus(HttpStatus.OK)
    fun sendOtpCode(@PathVariable("id") id: Long) {
        log.info { "Отправка нового OTP пользователю $id" }
        userService.sendOtpCode(id)
    }

    @PutMapping(value = ["/{id}/verify-email"])
    @Operation(summary = "Верификация электронной почты через OTP")
    @ResponseStatus(HttpStatus.OK)
    fun verifyEmail(@PathVariable("id") id: Long, @RequestParam("code") code: String) {
        log.info { "Верификация электронной почты через OTP" }
        userService.verifyEmail(id, code)
    }

    @Operation(summary = "Информация о текущем пользователе")
    @GetMapping(value = ["/me"])
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentUser(): ResponseEntity<UserResponse?> {
        log.info { "Информация о текущем пользователе" }
        val response = userService.currentUser()
        return if (response == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } else {
            ResponseEntity.ok(response)
        }
    }

    @Operation(
        summary = "Информация о пользователе",
        description = "Требуется роль ADMIN")
    @GetMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun getById(@PathVariable id: Long): UserResponse? {
        log.info { "Информация о пользователе" }
        return userService.getById(id)
    }

    @Operation(summary = "Смена пароля пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = ["/me/change-password"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun changePassword(@RequestBody @Valid request: ChangePasswordRequest): ResponseEntity<Unit> {
        log.info { "Смена пароля пользователя" }

        if (userService.getOTPCodeCurrentUser(request.verificationCode)
            && authenticationService.changePassword(request.currentPassword, request.newPassword)) {
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }
}