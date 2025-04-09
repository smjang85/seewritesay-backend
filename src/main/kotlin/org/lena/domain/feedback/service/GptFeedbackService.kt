package org.lena.domain.feedback.service

import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.config.security.CustomUserPrincipal

interface GptFeedbackService {
    fun generateFeedback(user: CustomUserPrincipal, sentence: String, imageId: Long): GptFeedbackResponseDto
}