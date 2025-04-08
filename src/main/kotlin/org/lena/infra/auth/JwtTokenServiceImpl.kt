package org.lena.infra.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import mu.KLogging
import org.lena.domain.auth.JwtTokenService
import org.lena.domain.user.entity.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtTokenServiceImpl : JwtTokenService {

    companion object : KLogging()

    @Value("\${jwt.secret}")
    private lateinit var secretKeyString: String

    @Value("\${jwt.validity-ms}")
    private var validityInMilliseconds: Long = 0

    override fun createToken(user: User): String {
        require(!user.email.isNullOrBlank()) { "이메일은 비어 있을 수 없습니다." }

        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))
        logger.info("createToken user.id $user.id" )
        return Jwts.builder()
            .setSubject(user.email) // 기본 subject는 email
            .setIssuedAt(now)
            .setExpiration(validity)
            .claim("id", user.id)
            .claim("name", user.name ?: "") // 선택적으로 name도 같이 추가 가능
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    override fun extractId(token: String): Long? {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        // ⭐ Number로 안전하게 가져와서 Long으로 변환
        return (claims["id"] as? Number)?.toLong()
    }

    override fun extractEmail(token: String): String {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    override fun extractName(token: String): String? {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["name"] as? String
    }
}