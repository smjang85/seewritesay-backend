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
        val expiration = Date(now.time + validityInMilliseconds) // 유효 기간 설정

        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))
        logger.debug("createToken user.id ${user.id}")

        return Jwts.builder()
            .setSubject(user.email) // 기본 subject는 email
            .setIssuedAt(now)
            .setExpiration(expiration)
            .claim("id", user.id)
            .claim("name", user.name ?: "") // 선택적으로 name도 추가 가능
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    // 토큰에서 ID 추출
    override fun extractId(token: String): Long? {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        // Number 타입으로 안전하게 가져와 Long으로 변환
        return (claims["id"] as? Number)?.toLong()
    }

    // 토큰에서 이메일 추출
    override fun extractEmail(token: String): String {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    // 토큰에서 이름 추출
    override fun extractName(token: String): String? {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["name"] as? String
    }

    // 토큰 만료 여부 확인
    override fun isTokenExpired(token: String): Boolean {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString))

        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        val expirationDate = claims.expiration
        return expirationDate.before(Date()) // 만료된 경우 true 반환
    }

    override fun refreshToken(user: User): String {
        return createToken(user) // 기존의 토큰 생성 로직을 재사용
    }
}
