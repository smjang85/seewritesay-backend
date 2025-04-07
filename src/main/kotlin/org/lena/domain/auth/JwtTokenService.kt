package org.lena.domain.auth

interface JwtTokenService {
    fun createToken(email: String?): String
    fun extractEmail(token: String): String
    fun extractName(token: String): String?
}
