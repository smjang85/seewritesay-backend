package org.lena.controller.api

import mu.KotlinLogging
import org.lena.dto.user.CustomUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/protected")
class ProtectedController {

    @GetMapping("/hello")
    fun hello(): String {
        logger.info { "🔐 보호된 API 접근 성공!" }
        return "Hello, authenticated user!"
    }

    @GetMapping("/settings")
    fun getUserSettings(@AuthenticationPrincipal user: CustomUser): Map<String, Any> {
        if (user == null) {
            throw RuntimeException("❌ 사용자 정보가 없습니다. JWT 인증을 확인하세요.")
        }

        return mapOf(
            "maxFeedbackCount" to 5,
            "remainingFeedbackCount" to 3,
            "username" to user.name
        )
    }
}
