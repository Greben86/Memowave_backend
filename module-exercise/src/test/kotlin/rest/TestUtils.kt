package dev.greben.memowave.rest

import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_LOGIN
import dev.greben.memowave.utils.Constants.AUTH_CLAIMS_ROLE
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.apache.commons.lang3.time.DateUtils
import java.util.*
import java.util.Map
import javax.crypto.SecretKey

object TestUtils {

    fun generateToken(email: String, role: String, jwtSigningKey: String): String {
        val currentTime = Date(System.currentTimeMillis())
        val keyBytes = Decoders.BASE64.decode(jwtSigningKey)
        return Jwts.builder()
            .claims()
            .add(Map.of<String?, String?>(AUTH_CLAIMS_LOGIN, email, AUTH_CLAIMS_ROLE, role))
            .and()
            .subject(email)
            .issuedAt(currentTime)
            .expiration(DateUtils.addMinutes(currentTime, 1))
            .signWith<SecretKey?>(Keys.hmacShaKeyFor(keyBytes), Jwts.SIG.HS256)
            .compact()
    }
}