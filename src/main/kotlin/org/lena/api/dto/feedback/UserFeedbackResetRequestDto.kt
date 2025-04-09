package org.lena.api.dto.feedback

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class UserFeedbackResetRequestDto(
    @field:NotNull(message = "이미지 ID는 필수입니다.")
    val imageId: Long,

    @field:Min(value = 0, message = "횟수는 0 이상이어야 합니다.")
    val count: Int
)