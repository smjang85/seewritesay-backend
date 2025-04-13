package org.lena.api.controller.feedback

import jakarta.validation.Valid
import mu.KotlinLogging
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.GptFeedbackRequestDto
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.feedback.service.GptFeedbackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("/api/v1/ai/feedback")
@Tag(name = "GPT 피드백", description = "GPT 작문 피드백 관련 API")
class GptFeedbackController(
    private val gptFeedbackService: GptFeedbackService
) {

    private val logger = KotlinLogging.logger {}

    @PostMapping("/generate")
    @Operation(
        summary = "GPT 피드백 생성",
        description = "사용자의 문장과 이미지 ID를 기반으로 GPT가 피드백을 생성합니다."
    )
    fun generateFeedback(
        @RequestBody @Valid request: GptFeedbackRequestDto,
        @CurrentUser user: CustomUserPrincipal
    ): ResponseEntity<ApiResponse<GptFeedbackResponseDto>> {
        logger.debug { "GPT 피드백 생성 요청 - userId=${user.id}, imageId=${request.imageId}" }

        val result = gptFeedbackService.generateFeedback(request.sentence, request.imageId)

        return ResponseEntity.ok(ApiResponse.success(result, "GPT 피드백 생성 완료"))
    }
}
