package org.lena.domain.auth

interface OAuthService {
    fun verifyGoogleIdToken(idToken: String): OAuthUserInfo
    fun verifyAppleIdToken(idToken: String): OAuthUserInfo
}