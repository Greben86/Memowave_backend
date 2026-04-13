package dev.greben.memowave.configuration

import dev.greben.memowave.clients.AuthClient
import dev.greben.memowave.dto.SignInRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException

@Component
class ExternalJwtAuthProvider(
    private val authClient: AuthClient
) : AuthenticationProvider {
    companion object {
        val log = KotlinLogging.logger {}
    }

    override fun authenticate(authentication: Authentication): Authentication {
        log.info { " --> authenticate: $authentication" }
        val username = authentication.name
        val password = authentication.credentials.toString()

        try {
            // Вызов внешнего сервиса за токеном
            val response = authClient.signIn(SignInRequest(email = username, password = password))
            log.info { " --> response: $response" }

            val token = response.token
            log.info { " --> token: $token" }

            // TODO переделать
            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER")) // лучше получать из ответа

            val authToken = UsernamePasswordAuthenticationToken(token, username, authorities)
            val context = SecurityContextHolder.createEmptyContext();
            context.authentication = authToken
            SecurityContextHolder.setContext(context)

            return authToken
        } catch (ex: HttpClientErrorException.Unauthorized) {
            throw BadCredentialsException("Invalid username or password", ex)
        } catch (ex: HttpClientErrorException.Forbidden) {
            throw DisabledException("User is disabled", ex)
        } catch (ex: HttpClientErrorException) {
            throw AuthenticationServiceException("Authentication service error: ${ex.statusCode}", ex)
        } catch (ex: ResourceAccessException) {
            throw AuthenticationServiceException("Network error: Unable to reach authentication service", ex)
        } catch (ex: Exception) {
            throw AuthenticationServiceException("Unexpected error during authentication", ex)
        }
    }

    override fun supports(authentication: Class<*>): Boolean =
        UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}