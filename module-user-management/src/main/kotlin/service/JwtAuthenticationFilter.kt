package dev.greben.memowave.service

import dev.greben.memowave.utils.Constants.AUTH_BEARER_PREFIX
import dev.greben.memowave.utils.Constants.AUTH_HEADER_NAME
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.lang.NonNull
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

        val authHeader = request.getHeader(AUTH_HEADER_NAME)
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, AUTH_BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        val jwt: String = authHeader.substring(AUTH_BEARER_PREFIX.length)
        val username: String = jwtService.extractUserName(jwt)

        if (StringUtils.isNotEmpty(username as CharSequence?)
            && SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            val userDetails: UserDetails = userService.userDetailsService()
                    .loadUserByUsername(username)

            // Если токен валиден, то аутентифицируем пользователя
            if (jwtService.isTokenValid(jwt, userDetails)) {
                val context = SecurityContextHolder.createEmptyContext()

                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                )

                authToken.setDetails(authHeader)
                context.setAuthentication(authToken)
                SecurityContextHolder.setContext(context)
            }
        }
        filterChain.doFilter(request, response)
    }
}