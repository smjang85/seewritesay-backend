package org.lena.config.resolver

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.SigningKeyResolverAdapter
import java.net.URL
import java.security.PublicKey

class ApplePublicKeyResolver : SigningKeyResolverAdapter() {

    companion object {
        private const val JWK_URL = "https://appleid.apple.com/auth/keys"
    }

    @Volatile
    private var publicKeys: Map<String, PublicKey> = loadKeys()

    private fun loadKeys(): Map<String, PublicKey> {
        return try {
            val jwkSet = JWKSet.load(URL(JWK_URL))
            jwkSet.keys.filterIsInstance<RSAKey>().associateBy({ it.keyID }, { it.toRSAPublicKey() })
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override fun resolveSigningKey(header: JwsHeader<*>, claims: Claims): PublicKey? {
        return publicKeys[header.keyId]
    }

    fun getPublicKeyWithFallback(kid: String): PublicKey {
        // 1차 시도
        publicKeys[kid]?.let { return it }

        // fallback 시도
        publicKeys = loadKeys()

        return publicKeys[kid] ?: throw RuntimeException("해당 kid로 public key를 찾을 수 없습니다: $kid")
    }
}
