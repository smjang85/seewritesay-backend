package org.lena.api.controller.auth

import org.lena.api.common.annotation.CurrentUser
import org.lena.domain.auth.JwtTokenService
import org.lena.domain.user.service.UserService
import org.lena.config.security.CustomUserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
    private val userService: UserService  // UserService를 통해 사용자 정보 확인
) {

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestHeader("Authorization") token: String,
        @CurrentUser user: CustomUserPrincipal?  // @CurrentUser를 사용하여 CustomUserPrincipal 객체 주입
    ): ResponseEntity<String> {
        // 사용자 정보가 없으면 오류 반환
        requireNotNull(user) { "사용자 정보가 없습니다." }

        val currentToken = token.removePrefix("Bearer ")

        return try {
            // JWT가 만료되었는지 확인
            if (jwtTokenService.isTokenExpired(currentToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("토큰이 만료되었습니다. 다시 로그인해주세요.")
            } else {
                // 토큰에서 사용자 ID 추출
                val userId = user.id ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자입니다.")
                val foundUser = userService.findById(userId)

                // 새로 발급된 토큰 생성
                val newToken = jwtTokenService.createToken(foundUser)
                ResponseEntity.ok("Bearer $newToken")
            }
        } catch (e: Exception) {
            // 예외 처리: 토큰이 유효하지 않거나 다른 오류가 발생했을 때
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 처리에 실패했습니다.")
        }
    }
}
