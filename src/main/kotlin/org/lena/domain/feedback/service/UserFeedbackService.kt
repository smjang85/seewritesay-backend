package org.lena.domain.feedback.service


import org.lena.api.dto.feedback.user.UserFeedbackResponseDto

interface UserFeedbackService {
    fun getRemainingCount(userId: Long, imageId: Long): UserFeedbackResponseDto
    fun decrementWritingFeedbackCount(userId: Long, imageId: Long)
}
