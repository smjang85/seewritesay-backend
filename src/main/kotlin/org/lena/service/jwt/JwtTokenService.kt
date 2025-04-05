package org.lena.service.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtTokenService {

    @Value("\${jwt.secret}")
    private lateinit var secretKeyString: String

    @Value("\${jwt.validity-ms}")
    private var validityInMilliseconds: Long = 0

    fun createToken(email: String?): String {
        require(!email.isNullOrBlank()) { "이메일은 비어 있을 수 없습니다." }

        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractEmail(token: String): String {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun extractName(token: String): String? {

        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))


        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["name"] as? String
    }
}
