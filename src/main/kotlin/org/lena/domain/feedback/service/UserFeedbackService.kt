package org.lena.domain.feedback.service


import org.lena.api.dto.feedback.UserFeedbackRequestDto
import org.lena.api.dto.feedback.UserFeedbackResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.user.entity.User
import org.lena.domain.image.entity.Image

interface UserFeedbackService {
    fun getRemainingCount(userId: Long, imageId: Long): UserFeedbackResponseDto
    fun decrementWritingFeedbackCount(userId: Long, imageId: Long)
}
