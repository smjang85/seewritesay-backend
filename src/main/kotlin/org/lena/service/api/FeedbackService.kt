package org.lena.service.api

import mu.KotlinLogging
import org.lena.dto.user.CustomUser
import org.lena.dto.api.FeedbackRequest
import org.lena.service.img.ImgService
import org.lena.util.GptClient
import org.springframework.stereotype.Service

@Service
class FeedbackService(
    private val gptClient: GptClient,
    private val imgService: ImgService
) {
    private val logger = KotlinLogging.logger {}

    fun saveFeedback(user: CustomUser, request: FeedbackRequest) {
        // TODO: DB 연동 예정 - 현재는 로그만 출력
        logger.info { "✅ 피드백 저장: ${request.sentence} (${user.email})" }
    }

    fun getFeedbackHistory(user: CustomUser): List<Map<String, Any>> {
        // TODO: DB 연동 예정 - 현재는 샘플 데이터 반환
        return listOf(
            mapOf(
                "sentence" to "I goed to the park.",
                "correction" to "I went to the park.",
                "feedback" to "The verb 'goed' is incorrect. Use 'went' instead.",
                "imageId" to "scene01"
            )
        )
    }

    fun generateFeedback(user: CustomUser, sentence: String, imageId: String): Map<String, String> {
        val imageDesc = imgService.getDescriptionByImgId(imageId)
        logger.info { "📝 imageDesc: $imageDesc" }

        val gptResponse = gptClient.getFeedback(sentence, imageDesc)
        logger.info { "🤖 GPT 응답: $gptResponse" }

        val correctionLine = gptResponse.lines().find { it.startsWith("Correction:") }?.removePrefix("Correction:")?.trim()
        val feedbackLine = gptResponse.lines().find { it.startsWith("Feedback:") }?.removePrefix("Feedback:")?.trim()

        return mapOf(
            "correction" to (correctionLine ?: sentence),
            "feedback" to (feedbackLine ?: "")
        )

    }
}