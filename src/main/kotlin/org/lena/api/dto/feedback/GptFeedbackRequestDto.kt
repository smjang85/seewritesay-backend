package org.lena.api.dto.feedback

data class GptFeedbackRequestDto(
    val sentence: String,
    val imageId: String
)
