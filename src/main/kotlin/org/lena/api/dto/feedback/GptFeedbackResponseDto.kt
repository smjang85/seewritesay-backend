package org.lena.api.dto.feedback

data class GptFeedbackResponseDto(
    val correction: String,
    val feedback: String,
    val grade: String
)