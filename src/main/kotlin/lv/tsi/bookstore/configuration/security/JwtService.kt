package lv.tsi.bookstore.configuration.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${application.security.jwt.secret-key}")
    private val secretKey: String,

    @Value("\${application.security.jwt.expiration}")
    private val expiration: Long,
) {

    fun generateAccessToken(user: UserDetails, extraClaims: Map<String, Any> = mapOf()): String {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(user.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(generateSigningKey())
            .compact()
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = Jwts.parser()
            .verifyWith(generateSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload

        return claimsResolver(claims)
    }

    private fun generateSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    fun isTokenValid(token: String, user: UserDetails): Boolean {
        val username = extractUsername(token)
        val expiration = extractExpiration(token)
        return username == user.username && expiration.after(Date())
    }
}
