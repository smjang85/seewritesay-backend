package org.lena.infra.auth

import com.google.firebase.auth.FirebaseAuth
import io.jsonwebtoken.Jwts
import org.lena.config.resolver.ApplePublicKeyResolver
import org.lena.domain.auth.OAuthService
import org.lena.domain.auth.OAuthUserInfo
import org.springframework.stereotype.Service

@Service
class OAuthServiceImpl : OAuthService {
    override fun verifyGoogleIdToken(idToken: String): OAuthUserInfo {
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
        val email = decodedToken.email ?: throw RuntimeException("이메일 없음")
        val name = decodedToken.name ?: "No Name"

        return OAuthUserInfo(
            email = email, name = name, sub = ""
        )
    }

    override fun verifyAppleIdToken(idToken: String): OAuthUserInfo {
        val resolver = ApplePublicKeyResolver()

        try {
            val jwt = Jwts.parserBuilder()
                .setSigningKeyResolver(resolver)
                .requireIssuer("https://appleid.apple.com")
                .requireAudience("com.seewritesay.app")
                .build()
                .parseClaimsJws(idToken)

            val claims = jwt.body
            val email = claims["email"] ?: throw RuntimeException("이메일 없음")
            val sub = claims["sub"] as String

            return OAuthUserInfo(email = email.toString(), sub = sub, name = "")
        } catch (e: Exception) {
            try {
                // fallback: 키 직접 가져와서 재시도
                val fallbackJwt = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJws(idToken)

                val kid = fallbackJwt.header.keyId
                val publicKey = resolver.getPublicKeyWithFallback(kid)

                val jwt = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer("https://appleid.apple.com")
                    .requireAudience("com.seewritesay.app")
                    .build()
                    .parseClaimsJws(idToken)

                val claims = jwt.body
                val email = claims["email"] ?: throw RuntimeException("이메일 없음")
                val sub = claims["sub"] as String

                return OAuthUserInfo(email = email.toString(), sub = sub, name = "")
            } catch (ex: Exception) {
                throw RuntimeException("Apple ID Token 검증 실패", ex)
            }
        }
    }


}