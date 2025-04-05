package org.lena.controller.api

import mu.KotlinLogging
import org.lena.dto.user.CustomUser
import org.lena.dto.api.FeedbackRequest
import org.lena.service.api.FeedbackService
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/feedback")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @PostMapping
    fun submitFeedback(
        @RequestBody request: FeedbackRequest,
        @AuthenticationPrincipal user: CustomUser?
    ): Map<String, Any> {
        logger.info("!!!! submitFeedback start")
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다. JWT 인증을 확인하세요.")
        logger.info { "✍️ 피드백 제출: ${request.sentence} (${user.email})" }
        feedbackService.saveFeedback(user, request)
        return mapOf("result" to "ok")
    }

    @GetMapping
    fun getFeedbackHistory(@AuthenticationPrincipal user: CustomUser?): List<Map<String, Any>> {
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        logger.info { "📚 피드백 히스토리 요청: ${user.email}" }
        return feedbackService.getFeedbackHistory(user)
    }

    @PostMapping("/generate")
    fun getFeedback(
        authentication: Authentication,  // 직접 인증 정보 받기
        @RequestBody request: Map<String, String>
    ): Map<String, String> {
        logger.info("!!!! getFeedback start")
        val user = authentication.principal as CustomUser
        val sentence = request["sentence"] ?: throw IllegalArgumentException("Missing sentence")
        val imageId = request["imageId"] ?: "unknown"

        return feedbackService.generateFeedback(user, sentence, imageId)
    }

}
