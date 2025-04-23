package org.lena.config.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import mu.KLogging
import org.lena.domain.auth.JwtTokenService
import org.lena.domain.user.service.UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.text.get

@Component
class OAuth2SuccessHandler(
    private val jwtTokenService: JwtTokenService,
    private val userService: UserService
) : AuthenticationSuccessHandler {

    companion object : KLogging()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val email = oAuth2User.attributes["email"] as String
        //val name = oAuth2User.attributes["name"] as String



        // ✅ 최종 로그인 일시 업데이트
        val user = userService.registerOrUpdate(email)

        logger.debug("✅ 구글 로그인 성공: $user.id , name:  ($email)")


        val jwt = jwtTokenService.createToken(user)
        val encodedJwt = URLEncoder.encode(jwt, StandardCharsets.UTF_8.toString())
        logger.debug("✅ jwt: $jwt")
        logger.debug("✅ encodedJwt: $encodedJwt")

        response.sendRedirect("seewritesay://auth/googleAuth/callback?token=$encodedJwt")
    }
}