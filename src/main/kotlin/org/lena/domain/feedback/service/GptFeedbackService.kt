package org.lena.domain.feedback.service

import org.lena.api.dto.feedback.GptFeedbackRequestDto
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.api.dto.user.CustomUserDto

interface GptFeedbackService {
    fun saveFeedback(user: CustomUserDto, request: GptFeedbackRequestDto)
    fun getFeedbackHistory(user: CustomUserDto): List<Map<String, Any>>
    fun generateFeedback(user: CustomUserDto, sentence: String, imageId: String): GptFeedbackResponseDto
}