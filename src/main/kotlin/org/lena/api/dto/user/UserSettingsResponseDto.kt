package org.lena.api.dto.user

data class UserSettingsResponseDto(
    val username: String,
    val maxFeedbackCount: Int,
    val remainingFeedbackCount: Int
)
