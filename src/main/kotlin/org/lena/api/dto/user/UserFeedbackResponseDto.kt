package org.lena.api.dto.user

import org.lena.domain.user.entity.User

data class UserFeedbackResponseDto(
    val writingRemainingCount: Int?,
    val readingRemainingCount: Int?
) {
    companion object {
        fun fromEntity(user: User): UserFeedbackResponseDto {
            return UserFeedbackResponseDto(
                writingRemainingCount = user.writingRemainingCount,
                readingRemainingCount = user.readingRemainingCount
            )
        }
    }
}