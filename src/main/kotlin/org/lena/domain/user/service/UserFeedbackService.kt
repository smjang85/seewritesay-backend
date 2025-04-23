package org.lena.domain.user.service

interface UserFeedbackService {
    fun decrementWritingFeedbackCount(userId: Long)
    fun decrementReadingFeedbackCount(userId: Long)
}