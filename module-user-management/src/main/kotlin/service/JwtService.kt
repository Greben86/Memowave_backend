package dev.greben.memowave.service

import dev.greben.memowave.entities.User
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_EMAIL
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_LOGIN
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_ROLE
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_USER_ID
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import javax.crypto.SecretKey

/**
 * Сервис для JWT
 */
@Service
class JwtService(
    // Уникальный ключ для генерации токена
    @Value("\${security.token.signing.key}")
    val jwtSigningKey: String,

    // Время жизни токена в миллисекундах
    @Value("\${security.token.expiration.minutes}")
    val jwtExpirationMinutes: Int
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
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String?, Any?>()
        if (userDetails is User) {
            claims[AUTH_CLAIMS_USER_ID] = userDetails.id
            claims[AUTH_CLAIMS_LOGIN] = userDetails.getUsername()
            claims[AUTH_CLAIMS_ROLE] = userDetails.getUserRole()
            claims[AUTH_CLAIMS_EMAIL] = userDetails.getEmail()
        }
        return generateToken(claims, userDetails)
    }

    /**
     * Проверка токена на валидность
     *
     * @param token токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val userName = extractUserName(token)
        return userName == userDetails.username && !isTokenExpired(token)
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
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return токен
     */
    private fun generateToken(extraClaims: MutableMap<String?, Any?>?, userDetails: UserDetails): String {
        val currentTime = Date(System.currentTimeMillis())
        return Jwts.builder()
            .claims().add(extraClaims).and()
            .subject(userDetails.username)
            .issuedAt(currentTime)
            .expiration(DateUtils.addMinutes(currentTime, jwtExpirationMinutes))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact()
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private fun isTokenExpired(token: String?): Boolean =
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
    private fun extractAllClaims(token: String?): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
    }

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