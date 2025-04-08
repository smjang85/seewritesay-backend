package org.lena.api.controller.feedback

import mu.KotlinLogging
import org.lena.api.dto.feedback.GptFeedbackRequestDto
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.api.dto.user.CustomUserDto
import org.lena.domain.feedback.service.GptFeedbackService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ai/feedback")
class GptFeedbackController(
    private val gptFeedbackService: GptFeedbackService
) {

    private val logger = KotlinLogging.logger {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun submitFeedback(
        @RequestBody request: GptFeedbackRequestDto,
        @AuthenticationPrincipal user: CustomUserDto?
    ) {
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        gptFeedbackService.saveFeedback(user, request)
    }

    @GetMapping
    fun getFeedbackHistory(@AuthenticationPrincipal user: CustomUserDto?): List<Map<String, Any>> {
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        return gptFeedbackService.getFeedbackHistory(user)
    }

    @PostMapping("/generate")
    fun getFeedback(
        @RequestBody request: GptFeedbackRequestDto,
        @AuthenticationPrincipal user: CustomUserDto?
    ): GptFeedbackResponseDto {
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        val sentence = request.sentence
        val imageId = request.imageId

        return gptFeedbackService.generateFeedback(user, sentence, imageId)
    }

}