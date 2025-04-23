package org.lena.api.dto.feedback.writing

data class AiWritingFeedbackResponseDto(
    val correction: String,
    val feedback: String,
    val grade: String
)