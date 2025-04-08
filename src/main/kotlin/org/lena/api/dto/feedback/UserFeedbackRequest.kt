package org.lena.api.dto.feedback

data class UserFeedbackRequest(
    val userId: Long,
    val imageId: Long
)
