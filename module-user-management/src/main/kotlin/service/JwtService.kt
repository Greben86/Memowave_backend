package dev.greben.memowave.service

import dev.greben.memowave.entities.Session
import dev.greben.memowave.entities.User
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_EMAIL
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_LOGIN
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_ROLE
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_SESSION
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_TYPE
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_USER_ID
import dev.greben.memowave.utils.TokenType
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultClaims
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
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

    // Время жизни refresh токена в миллисекундах
    @Value("\${security.token.expiration.refresh.minutes}")
    val jwtRefreshExpirationMinutes: Int,

    // Время жизни access токена в миллисекундах
    @Value("\${security.token.expiration.access.minutes}")
    val jwtAccessExpirationMinutes: Int
) {
    companion object {
        val log = KotlinLogging.logger {}
    }

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    fun extractUserName(token: String): String =
        extractClaim<String>(token, Claims::getSubject)

    /**
     * Извлечение email пользователя из токена
     *
     * @param token токен
     * @return email пользователя
     */
    fun extractUserEmail(token: String): String =
        extractClaim(token) { it[AUTH_CLAIMS_EMAIL].toString() }

    /**
     * Извлечение идентификатора сессии из токена
     *
     * @param token токен
     * @return идентификатор сессии пользователя
     */
    fun extractSessionId(token: String): Integer =
        extractClaim(token) { it[AUTH_CLAIMS_SESSION] as Integer }

    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    fun generateRefreshToken(userDetails: UserDetails, session: Session): String {
        val claims = HashMap<String?, Any?>()
        if (userDetails is User) {
            claims[AUTH_CLAIMS_TYPE] = TokenType.REFRESH
            claims[AUTH_CLAIMS_USER_ID] = userDetails.id
            claims[AUTH_CLAIMS_LOGIN] = userDetails.username
            claims[AUTH_CLAIMS_ROLE] = userDetails.userRole
            claims[AUTH_CLAIMS_EMAIL] = userDetails.email
            claims[AUTH_CLAIMS_SESSION] = session.id
        }
        return generateRefreshToken(claims, userDetails.username)
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
     * Проверка, что это refresh токен
     *
     * @param token токен
     * @return true, если это refresh токен
     */
    fun isTokenRefresh(token: String): Boolean =
        extractClaim(token) { TokenType.REFRESH.name == it[AUTH_CLAIMS_TYPE] as String }

    /**
     * Проверка, что это access токен
     *
     * @param token токен
     * @return true, если это access токен
     */
    fun isTokenAccess(token: String): Boolean =
        extractClaim(token) { TokenType.ACCESS.name == it[AUTH_CLAIMS_TYPE] as String }

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
     * Генерация refresh токена
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return refresh токен
     */
    private fun generateRefreshToken(extraClaims: MutableMap<String?, Any?>?, username: String): String {
        val currentTime = Date(System.currentTimeMillis())
        return Jwts.builder()
            .claims().add(extraClaims).and()
            .subject(username)
            .issuedAt(currentTime)
            .expiration(DateUtils.addMinutes(currentTime, jwtRefreshExpirationMinutes))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact()
    }

    /**
     * Создание нового access токена из refresh токена
     *
     * @param refreshToken refresh токен
     * @return access токен
     */
    fun generateAccessTokenFromRefreshToken(refreshToken: String): String {
        val currentTime = Date(System.currentTimeMillis())
        val extraClaims: Claims = extractAllClaims(refreshToken)
        val claims = HashMap<String?, Any?>()
        claims.putAll(extraClaims)
        claims[AUTH_CLAIMS_TYPE] = TokenType.ACCESS

        // Создаём новый Access Token
        return Jwts.builder()
            .claims().add(claims).and()
            .subject(extraClaims.subject)
            .issuedAt(currentTime)
            .expiration(DateUtils.addMinutes(currentTime, jwtAccessExpirationMinutes))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact()
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
    private fun getSigningKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSigningKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}