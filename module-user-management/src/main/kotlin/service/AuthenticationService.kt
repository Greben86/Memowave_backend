package dev.greben.memowave.service

import dev.greben.memowave.dto.JwtAuthenticationResponse
import dev.greben.memowave.dto.SignInRequest
import dev.greben.memowave.dto.SignUpRequest
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис аутентификации
 */
@Service
class AuthenticationService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    fun signUp(request: SignUpRequest): JwtAuthenticationResponse {
        val user = userService.register(request, passwordEncoder.encode(request.password))
        val jwt: String = jwtService.generateToken(user)
        return JwtAuthenticationResponse(jwt)
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    fun signIn(request: SignInRequest): JwtAuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        val user = userService
            .userDetailsServiceByEmail()
            .loadUserByUsername(request.email)

        val jwt = jwtService.generateToken(user)
        return JwtAuthenticationResponse(jwt)
    }

    /**
     * Смена пароля пользователя
     *
     * @param currentPassword текущий пароль
     * @param newPassword новый пароль
     * @return true если пароль успешно изменен, иначе false
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun changePassword(currentPassword: String, newPassword: String): Boolean {
        val user = userService.getCurrentUser() ?: return false

        // Проверка текущего пароля
        check(!passwordEncoder.matches(currentPassword, user.passwordHash)) {
            "Пароль пользователя '${user.username}' не правильный"
        }

        // Обновление пароля
        userService.changePassword(user, passwordEncoder.encode(newPassword))
        return true
    }
}