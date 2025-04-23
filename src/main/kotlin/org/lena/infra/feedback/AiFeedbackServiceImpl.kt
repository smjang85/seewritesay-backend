package org.lena.infra.feedback

import mu.KotlinLogging
import org.lena.api.dto.feedback.reading.AiReadingFeedbackResponseDto
import org.lena.api.dto.feedback.writing.AiWritingFeedbackResponseDto
import org.lena.domain.feedback.service.AiFeedbackService
import org.lena.domain.image.service.ImageService
import org.lena.infra.feedback.client.AzureReadingFeedbackClient
import org.lena.infra.feedback.client.GptWritingFeedbackClient
import org.lena.infra.util.FfmpegAudioConverter
import org.lena.infra.util.FfmpegAudioConverter.deleteFileIfExists
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.util.*

@Service
class AiFeedbackServiceImpl(
    private val gptWritingFeedbackClient: GptWritingFeedbackClient,
    private val azureReadingFeedbackClient: AzureReadingFeedbackClient,
    private val imageService: ImageService
) : AiFeedbackService {

    private val logger = KotlinLogging.logger {}

    override fun generateWritingFeedback(sentence: String, imageId: Long): AiWritingFeedbackResponseDto {
        // 이미지에 대한 설명을 가져옵니다.
        val imageDesc = imageService.getDescriptionByImageId(imageId)
        logger.debug { "GptFeedbackServiceImpl>generateFeedback imageDesc: $imageDesc" }

        // GPT 클라이언트를 이용하여 작성 피드백을 생성합니다.
        val gptResponse = gptWritingFeedbackClient.generateWritingFeedback(sentence, imageDesc)
        logger.debug { "GptFeedbackServiceImpl>generateFeedback GPT 응답: $gptResponse" }

        return gptResponse
    }

    override fun generateReadingFeedback(
        sentence: String?,
        userId: Long,
        imageId: Long,
        file: MultipartFile
    ): AiReadingFeedbackResponseDto {
        val currentDate = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
        val uniqueId = UUID.randomUUID().toString()

        val tempPath = "/readingFeedback/temp/$userId/$imageId/request_${currentDate}_$uniqueId.wav"
        val resultPath = "/readingFeedback/$userId/$imageId/request_${currentDate}_$uniqueId.wav"

        FfmpegAudioConverter.convertToMono16k(file, tempPath, resultPath)

        val sentenceFromFile = azureReadingFeedbackClient.speechToText(resultPath)
        val azureResponse = azureReadingFeedbackClient.evaluatePronunciation(resultPath, sentenceFromFile, sentence)

        deleteFileIfExists(resultPath);

        return azureResponse
    }
}
