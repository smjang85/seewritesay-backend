package org.lena.infra.feedback

import mu.KotlinLogging
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.domain.feedback.service.GptFeedbackService
import org.lena.domain.image.service.ImageService
import org.lena.infra.feedback.client.GptFeedbackClient
import org.springframework.stereotype.Service

@Service
class GptFeedbackServiceImpl(
    private val gptFeedbackClient: GptFeedbackClient,
    private val imageService: ImageService
) : GptFeedbackService {

    private val logger = KotlinLogging.logger {}

    override fun generateFeedback(sentence: String, imageId: Long): GptFeedbackResponseDto {
        val imageDesc = imageService.getDescriptionByImageId(imageId)
        logger.debug { "GptFeedbackServiceImpl>generateFeedback imageDesc: $imageDesc" }

        val gptResponse = gptFeedbackClient.getFeedback(sentence, imageDesc)
        logger.debug { "GptFeedbackServiceImpl>generateFeedback GPT 응답: $gptResponse" }

        return gptResponse
    }
}
