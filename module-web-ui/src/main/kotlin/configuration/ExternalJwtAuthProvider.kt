package dev.greben.memowave.configuration

import dev.greben.memowave.clients.AuthClient
import dev.greben.memowave.dto.SignInRequest
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_ROLE
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultClaims
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
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
import java.util.function.Function
import javax.crypto.SecretKey

@Component
class ExternalJwtAuthProvider(
    // Уникальный ключ для проверки токена
    @Value("\${security.token.signing.key}")
    val jwtSigningKey: String,
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

            val role = extractUserRole(token)
            log.info { " --> role: $role" }
            if ("ROLE_ADMIN" != role) {
                log.warn {"Role '$role' not allowed"}
                throw SecurityException("Role '$role' not allowed")
            }

            val authorities = listOf(SimpleGrantedAuthority(role))
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

    /**
     * Извлечение роли пользователя из токена
     *
     * @param token токен
     * @return роль пользователя
     */
    private fun extractUserRole(token: String): String =
        extractClaim(token) { it[AUTH_CLAIMS_ROLE].toString() }

    /**
     * Извлечение данных из токена
     *
     * @param token токен
     * @param claimsResolvers функция извлечения данных
     * @param <T> тип данных
     * @return данные
     */
    private fun <T> extractClaim(token: String?, claimsResolvers: Function<Claims, T>): T =
        claimsResolvers.apply(extractAllClaims(token))

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private fun extractAllClaims(token: String?): Claims {
        try {
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
        } catch (ex: Exception) {
            log.error(ex) { ex.message }
            return DefaultClaims(mapOf<String, Any>())
        }
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private fun getSigningKey(): SecretKey =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey))
}