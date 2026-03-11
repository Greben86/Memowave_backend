package dev.greben.memowave.service

import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_ROLE
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_USER_ID
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.Date
import java.util.function.Function
import javax.crypto.SecretKey

/**
 * Сервис для JWT
 */
@Service
class JwtService(
    // Уникальный ключ для генерации токена
    @Value("\${security.token.signing.key}")
    val jwtSigningKey: String
) {

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    fun extractUserName(token: String): String =
        extractClaim<String>(token, Claims::getSubject)

    /**
     * Извлечение роли пользователя из токена
     *
     * @param token токен
     * @return роль пользователя
     */
    fun extractUserRole(token: String): GrantedAuthority =
        SimpleGrantedAuthority(extractClaim(token) {
            it[AUTH_CLAIMS_ROLE].toString()
        })

    /**
     * Извлечение id пользователя из токена
     *
     * @param token токен
     * @return роль пользователя
     */
    fun extractUserId(token: String): Long? =
        extractClaim(token) {
            it[AUTH_CLAIMS_USER_ID].toString().toLongOrNull()
        }

    /**
     * Извлечение данных из токена
     *
     * @param token токен
     * @param claimsResolvers функция извлечения данных
     * @param <T> тип данных
     * @return данные
     */
    private fun <T> extractClaim(token: String?, claimsResolvers: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolvers.apply(claims)
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    fun isTokenExpired(token: String?): Boolean =
        extractExpiration(token).before(Date())

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private fun extractExpiration(token: String?): Date =
        extractClaim<Date>(token, Claims::getExpiration)

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private fun extractAllClaims(token: String?): Claims =
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private fun getSigningKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSigningKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}