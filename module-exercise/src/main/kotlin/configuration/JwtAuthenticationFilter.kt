package dev.greben.memowave.configuration

import dev.greben.memowave.service.JwtService
import dev.greben.memowave.utils.Constants
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Получаем токен из заголовка
        val authHeader = request.getHeader(Constants.AUTH_HEADER_NAME)
        if (StringUtils.isEmpty(authHeader)
            || !StringUtils.startsWith(authHeader, Constants.AUTH_BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        val jwt: String = authHeader.substring(Constants.AUTH_BEARER_PREFIX.length)
        val username: String = jwtService.extractUserName(jwt)

        if (StringUtils.isNotEmpty(username as CharSequence)
            && SecurityContextHolder.getContext().authentication == null
        ) {
            // Если токен валиден, то аутентифицируем пользователя
            if (!jwtService.isTokenExpired(jwt)) {
                val context = SecurityContextHolder.createEmptyContext()

                val authToken = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    listOf(jwtService.extractUserRole(jwt))
                )

                authToken.details = authHeader
                context.authentication = authToken
                SecurityContextHolder.setContext(context)
            }
        }
        filterChain.doFilter(request, response)
    }
}