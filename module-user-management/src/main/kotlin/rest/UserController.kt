package dev.greben.memowave.rest

import dev.greben.memowave.dto.UserResponse
import dev.greben.memowave.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/users")
@Tag(name = "REST API: Пользователь")
class UserController(
    private val userService: UserService
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    @Operation(summary = "Редактирование пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(
        value = ["/edit/user", "/edit/user/"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("hasRole('ADMIN')")
    fun editUser(@RequestBody @Valid dto: UserResponse?): UserResponse? {
        log.info { "Редактирование пользователя" }
        return userService.saveUser(dto)
    }

    @Operation(summary = "Список всех пользователей, кроме администраторов")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = ["", "/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ADMIN')")
    fun getUsers(): List<UserResponse> {
        log.info { "Список всех пользователей, кроме администраторов" }
        return userService.getAllUsers()
    }

    @Operation(summary = "Удаление пользователя")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = ["/{id}/user", "/{id}/user/"])
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(@PathVariable("id") id: Long): ResponseEntity<Nothing> {
        log.info { "Удаление пользователя" }
        if (userService.getById(id) != null) {
            userService.deleteUser(id)
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }

    @PutMapping(value = ["/{id}/user/set-admin", "/{id}/user/set-admin/"])
    @Operation(summary = "Добавить роль ADMIN пользователю")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    fun setAdmin(@PathVariable("id") id: Long): ResponseEntity<Void> {
        log.info { "Добавление роли ADMIN пользователю" }
        userService.setAdmin(id)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Информация о пользователе")
    @GetMapping(value = ["/{id}/user", "/{id}/user/"])
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun getById(@PathVariable id: Long): UserResponse? {
        log.info { "Информация о пользователе" }
        return userService.getById(id)
    }
}