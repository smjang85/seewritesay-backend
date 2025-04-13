package org.lena.api.dto.feedback

data class UserFeedbackResponseDto(
    val writingRemainingCount: Int,
    val readingRemainingCount: Int
)