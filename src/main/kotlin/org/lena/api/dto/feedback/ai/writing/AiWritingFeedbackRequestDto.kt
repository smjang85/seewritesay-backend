package org.lena.api.dto.feedback.ai.writing

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AiWritingFeedbackRequestDto(

    @field:NotBlank(message = "문장은 비워둘 수 없습니다.")
    @field:Size(min = 5, message = "문장은 최소 5자 이상이어야 합니다.")
    @Schema(
        description = "GPT 피드백을 받을 문장",
        example = "I want to go to the park with my family.",
        required = true
    )
    val sentence: String,

    @field:NotNull(message = "imageId는 필수입니다.")
    @Schema(
        description = "이미지 식별자",
        example = "101",
        required = true
    )
    val imageId: Long
)