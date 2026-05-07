package dev.greben.memowave.service

import dev.greben.memowave.dto.JwtAuthenticationResponse
import dev.greben.memowave.dto.JwtRefreshRequest
import dev.greben.memowave.dto.SignInRequest
import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.entities.User
import io.github.oshai.kotlinlogging.KotlinLogging
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
    private val sessionService: SessionService,
    private val userService: UserService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    fun signUp(request: SignUpRequest): JwtAuthenticationResponse {
        val user = userService.register(request, passwordEncoder.encode(request.password))
        val session = sessionService.createSessionByName(request.session, user)
        val refreshToken = jwtService.generateRefreshToken(user, session)
        val accessToken = jwtService.generateAccessTokenFromRefreshToken(refreshToken)
        return JwtAuthenticationResponse(accessToken = accessToken, refreshToken = refreshToken, session = session.name!!)
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

        val session = sessionService.createSessionByName(request.session, user as User)
        val refreshToken = jwtService.generateRefreshToken(user, session)
        val accessToken = jwtService.generateAccessTokenFromRefreshToken(refreshToken)
        return JwtAuthenticationResponse(accessToken = accessToken, refreshToken = refreshToken, session = session.name!!)
    }

    /**
     * Обновление access токена
     *
     * @param request данные пользователя
     * @return токен
     */
    fun refreshAccessToken(request: JwtRefreshRequest): JwtAuthenticationResponse {
        val refreshToken = request.refreshToken
        check(jwtService.isTokenRefresh(refreshToken)) {
            "Это не Refresh Token"
        }

        check(!jwtService.isTokenExpired(refreshToken)) {
            "Refresh Token просрочен"
        }

        val sessionId: Integer = jwtService.extractSessionId(refreshToken)
        check(sessionService.isAllowedSessionById(sessionId.toLong())) {
            "Сессия не найдена"
        }

        val session = sessionService.getSessionById(sessionId.toLong())
        val accessToken = jwtService.generateAccessTokenFromRefreshToken(refreshToken)
        return JwtAuthenticationResponse(accessToken = accessToken, refreshToken = refreshToken, session = session!!.name!!)
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