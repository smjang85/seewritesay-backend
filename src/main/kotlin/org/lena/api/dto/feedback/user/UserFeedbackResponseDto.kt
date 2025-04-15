package org.lena.api.dto.feedback.user

import org.lena.domain.feedback.entity.UserFeedback

data class UserFeedbackResponseDto(
    val writingRemainingCount: Int,
    val readingRemainingCount: Int
) {
    companion object {
        fun fromEntity(userFeedback: UserFeedback): UserFeedbackResponseDto {
            return UserFeedbackResponseDto(
                writingRemainingCount = userFeedback.writing_remaining_count,
                readingRemainingCount = userFeedback.reading_remaining_count
            )
        }
    }
}