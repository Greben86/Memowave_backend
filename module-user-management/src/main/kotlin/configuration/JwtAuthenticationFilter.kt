package dev.greben.memowave.configuration

import dev.greben.memowave.service.JwtService
import dev.greben.memowave.service.UserService
import dev.greben.memowave.utils.Constants
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userService: UserService
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
        val email: String = jwtService.extractUserEmail(jwt)

        if (StringUtils.isNotEmpty(email as CharSequence)
            && SecurityContextHolder.getContext().authentication == null
        ) {
            val userDetails: UserDetails = userService.userDetailsServiceByEmail()
                    .loadUserByUsername(email)

            // Если это access токен и он валиден, то аутентифицируем пользователя
            if (jwtService.isTokenAccess(jwt) && jwtService.isTokenValid(jwt, userDetails)) {
                val context = SecurityContextHolder.createEmptyContext()

                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )

                authToken.details = authHeader
                context.authentication = authToken
                SecurityContextHolder.setContext(context)
            }
        }
        filterChain.doFilter(request, response)
    }
}