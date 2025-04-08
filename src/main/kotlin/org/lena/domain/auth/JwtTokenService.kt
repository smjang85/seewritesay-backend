package org.lena.domain.auth

import org.lena.domain.user.entity.User

interface JwtTokenService {
    fun createToken(user: User): String
    fun extractId(token: String): Long?
    fun extractEmail(token: String): String
    fun extractName(token: String): String?
}
