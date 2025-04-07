package org.lena.infra.feedback

import mu.KotlinLogging
import org.lena.api.dto.feedback.GptFeedbackRequestDto
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.api.dto.user.CustomUserDto
import org.lena.domain.feedback.service.GptFeedbackService
import org.lena.domain.image.service.ImageService
import org.lena.infra.feedback.client.GptFeedbackClient
import org.springframework.stereotype.Service

@Service
class GptFeedbackServiceImpl(
    private val gptClient: GptFeedbackClient,
    private val imageService: ImageService
) : GptFeedbackService {

    private val logger = KotlinLogging.logger {}

    override fun saveFeedback(user: CustomUserDto, request: GptFeedbackRequestDto) {
        // TODO: DB 연동 예정 - 현재는 로그만 출력
        logger.info { "✅ 피드백 저장: ${request.sentence} (${user.email})" }
    }

    override fun getFeedbackHistory(user: CustomUserDto): List<Map<String, Any>> {
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

    override fun generateFeedback(user: CustomUserDto, sentence: String, imageName: String): GptFeedbackResponseDto {
        val imageDesc = imageService.getDescriptionByImageName(imageName)
        logger.info { "📝 imageDesc: $imageDesc" }

        val gptResponse = gptClient.getFeedback(sentence, imageDesc)
        logger.info { "🤖 GPT 응답: $gptResponse" }

        val correctionLine = gptResponse.lines().find { it.startsWith("Correction:") }?.removePrefix("Correction:")?.trim()
        val feedbackLine = gptResponse.lines().find { it.startsWith("Feedback:") }?.removePrefix("Feedback:")?.trim()

        return GptFeedbackResponseDto(
            correction = correctionLine ?: sentence,
            feedback = feedbackLine ?: ""
        )
    }
}
