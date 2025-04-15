// 파일 위치: org.lena.domain.feedback.client.AiFeedbackClient.kt
package org.lena.domain.feedback.client

import org.lena.api.dto.feedback.ai.reading.AiReadingFeedbackResponseDto

interface AiFeedbackReadingClient {
    fun speechToText(audioFilePath: String): String
    fun evaluatePronunciation(audioFilePath: String, sentenceFromFile: String, sentence: String?): AiReadingFeedbackResponseDto

}
