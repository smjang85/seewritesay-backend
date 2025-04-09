package org.lena.api.dto.feedback

import jakarta.validation.constraints.NotNull

data class UserFeedbackRequestDto(
    @field:NotNull(message = "이미지 ID는 필수입니다.")
    val imageId: Long
)