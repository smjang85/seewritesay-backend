package org.lena.api.controller.feedback

import jakarta.validation.Valid
import mu.KotlinLogging
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.ai.writing.AiWritingFeedbackRequestDto
import org.lena.api.dto.feedback.ai.writing.AiWritingFeedbackResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.feedback.service.AiFeedbackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.lena.api.dto.feedback.ai.reading.AiReadingFeedbackResponseDto
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/ai/feedback/generate")
@Tag(name = "GPT 피드백", description = "GPT 작문 피드백 관련 API")
class AiFeedbackController(
    private val aiFeedbackService: AiFeedbackService
) {

    private val logger = KotlinLogging.logger {}

    @PostMapping("/writing")
    @Operation(
        summary = "GPT 피드백 생성",
        description = "사용자의 문장과 이미지 ID를 기반으로 GPT가 피드백을 생성합니다."
    )
    fun generateWritingFeedback(
        @RequestBody @Valid request: AiWritingFeedbackRequestDto,
        @CurrentUser user: CustomUserPrincipal
    ): ResponseEntity<ApiResponse<AiWritingFeedbackResponseDto>> {
        logger.debug { "GPT 피드백 생성 요청 - userId=${user.id}, imageId=${request.imageId}" }

        val result = aiFeedbackService.generateWritingFeedback(request.sentence, request.imageId)

        return ResponseEntity.ok(ApiResponse.success(result, "GPT 피드백 생성 완료"))
    }

    @PostMapping("/reading")
    @Operation(
        summary = "발음 피드백 생성",
        description = "사용자의 음성 파일을 기반으로 발음 피드백을 생성합니다."
    )
    fun generateReadingFeedback(
        @RequestParam("file") file: MultipartFile,
        @RequestParam(required = false) sentence: String?,
        @RequestParam imageId: Long,
        @CurrentUser user: CustomUserPrincipal
    ): ResponseEntity<ApiResponse<AiReadingFeedbackResponseDto>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "발음 피드백 생성 요청 - userId=${user.id}" }

        val result = aiFeedbackService.generateReadingFeedback(sentence, user.id, imageId, file)

        return ResponseEntity.ok(ApiResponse.success(result, "발음 피드백 생성 완료"))
    }
}
