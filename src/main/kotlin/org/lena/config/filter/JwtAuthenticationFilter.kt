package org.lena.config.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.lena.api.dto.user.CustomUserDto
import org.lena.domain.auth.JwtTokenService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("🛂 JWT 필터 동작 시작")

        val authHeader = request.getHeader("Authorization")
        val token = authHeader?.removePrefix("Bearer ") ?: return filterChain.doFilter(request, response)


        logger.info("✅ JWT token: $token")
        try {
            val id = jwtTokenService.extractId(token)
            val email = jwtTokenService.extractEmail(token)
            val name = jwtTokenService.extractName(token) // 이름도 토큰에서 추출 가능하다면

            logger.info("✅ JWT 인증 성공 id: $id")
            logger.info("✅ JWT 인증 성공: email $email")

            val user = CustomUserDto(
                id = id ?: 0L,
                name = name ?: "사용자", // null 가능성 고려해서 기본값 설정
                email = email
            )

            val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
            SecurityContextHolder.getContext().authentication = auth
        } catch (e: Exception) {
            logger.error("❌ JWT 검증 실패: ${e.message}")
        }
        filterChain.doFilter(request, response)
    }
}
