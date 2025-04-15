// 파일 위치: org.lena.domain.feedback.client.AiFeedbackClient.kt
package org.lena.domain.feedback.client

import org.lena.api.dto.feedback.ai.writing.AiWritingFeedbackResponseDto

interface AiWritingFeedbackClient {
    fun generateWritingFeedback(sentence: String, imageDesc: String): AiWritingFeedbackResponseDto
}
