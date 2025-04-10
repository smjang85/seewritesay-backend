// 파일 위치: org.lena.domain.feedback.client.AiFeedbackClient.kt
package org.lena.domain.feedback.client

import org.lena.api.dto.feedback.GptFeedbackResponseDto

interface AiFeedbackClient {
    fun getFeedback(sentence: String, imageDesc: String): GptFeedbackResponseDto
}
