package dev.greben.memowave.configuration

import dev.greben.memowave.clients.AuthClient
import dev.greben.memowave.dto.SignInRequest
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class ExternalJwtAuthProvider(private val authClient: AuthClient) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        // Вызов внешнего сервиса за токеном
        val response = authClient.signIn(
            SignInRequest(email = username, password = password))

        val token = response.token ?: throw BadCredentialsException("Внешний сервис не вернул токен")

        // TODO переделать
        val authToken = UsernamePasswordAuthenticationToken(token, username,
            listOf(SimpleGrantedAuthority("ROLE_ADMIN")))
        var context = SecurityContextHolder.createEmptyContext();
        context.authentication = authToken
        SecurityContextHolder.setContext(context)

        return authToken
    }

    override fun supports(authentication: Class<*>): Boolean =
        UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}