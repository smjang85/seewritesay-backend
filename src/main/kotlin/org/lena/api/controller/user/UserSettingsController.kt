package org.lena.api.controller.user

import mu.KotlinLogging
import org.lena.api.dto.user.CustomUserDto
import org.lena.api.dto.user.UserSettingsResponseDto
import org.lena.domain.user.service.UserService
import org.springframework.context.annotation.Profile
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/user")
class UserSettingsController(
    private val userService: UserService
) {

    @Profile("local")
    @GetMapping("/hello")
    fun hello(): String {
        logger.info { "🔐 보호된 API 접근 성공!" }
        return "Hello, authenticated user!"
    }

    @GetMapping("/settings")
    fun getUserSettings(@AuthenticationPrincipal user: CustomUserDto?): UserSettingsResponseDto{
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        return userService.getUserSettings(user)
    }
}
