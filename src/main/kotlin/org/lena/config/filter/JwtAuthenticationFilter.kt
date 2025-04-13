package org.lena.config.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.lena.config.security.CustomUserPrincipal
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
        logger.debug("🛂 JWT 필터 동작 시작")

        // Authorization 헤더에서 Bearer 토큰 추출
        val authHeader = request.getHeader("Authorization")
        val token = authHeader?.removePrefix("Bearer ") ?: return filterChain.doFilter(request, response)

        logger.debug("✅ JWT token: $token")

        try {
            // JWT 검증 및 정보 추출
            val id = jwtTokenService.extractId(token)
            val email = jwtTokenService.extractEmail(token)
            val name = jwtTokenService.extractName(token)

            // 만약 토큰이 만료되었으면, 유효하지 않다고 처리
            if (jwtTokenService.isTokenExpired(token)) {
                logger.warn("❌ JWT 만료됨: $token")
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("로그인 세션이 만료되었습니다. 다시 로그인 해주세요.")
                return
            }

            // JWT 인증 성공, 사용자 정보를 기반으로 인증 객체 생성
            logger.debug("✅ JWT 인증 성공 id: $id, email: $email")

            val user = CustomUserPrincipal(
                id = id ?: 0L,
                name = name ?: "사용자",
                email = email
            )

            val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
            SecurityContextHolder.getContext().authentication = auth

        } catch (e: Exception) {
            // 예외 처리 (JWT 파싱 실패, 잘못된 토큰 등)
            logger.error("❌ JWT 검증 실패: ${e.message}")
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("유효하지 않은 토큰입니다.")
            return
        }

        // 인증이 완료되면 필터 체인 진행
        filterChain.doFilter(request, response)
    }
}
