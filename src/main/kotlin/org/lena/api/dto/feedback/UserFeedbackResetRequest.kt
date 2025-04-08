package org.lena.api.dto.feedback

data class UserFeedbackResetRequest(
    val userId: Long,
    val imageId: Long,
    val count: Int
)