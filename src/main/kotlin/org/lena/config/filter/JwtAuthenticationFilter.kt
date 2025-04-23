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
        logger.debug("🛂 JWT 필터 동작 시작 : ${request.requestURI}")

        // 1. Authorization 헤더 확인
        val authHeader = request.getHeader("Authorization")
        logger.debug("🔍 Authorization 헤더: $authHeader")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("⚠️ Authorization 헤더가 없거나 'Bearer' 접두사가 없음 - 필터 통과")
            return filterChain.doFilter(request, response)
        }

        // 2. 토큰 추출
        val token = authHeader.removePrefix("Bearer ")
        logger.debug("✅ 추출된 JWT token: $token")

        try {
            // 3. 토큰에서 사용자 정보 추출
            val id = jwtTokenService.extractId(token)
            val email = jwtTokenService.extractEmail(token)
            val name = jwtTokenService.extractName(token)

            logger.debug("🧬 토큰에서 추출된 정보 - id: $id, email: $email, name: $name")

            if (jwtTokenService.isTokenExpired(token)) {
                logger.warn("❌ JWT 만료됨: $token")
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("로그인 세션이 만료되었습니다. 다시 로그인 해주세요.")
                return
            }

            logger.debug("✅ JWT 인증 성공")

            // 4. 인증 객체 생성 및 컨텍스트에 설정
            val user = CustomUserPrincipal(
                id = id ?: 0L,
                name = name ?: "사용자",
                email = email
            )

            val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
            SecurityContextHolder.getContext().authentication = auth

        } catch (e: Exception) {
            logger.error("❌ JWT 검증 실패: ${e.message}", e)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("유효하지 않은 토큰입니다.")
            return
        }

        // 5. 다음 필터로 진행
        logger.debug("➡️ 인증 완료, 필터 체인 진행")
        filterChain.doFilter(request, response)
    }

}
