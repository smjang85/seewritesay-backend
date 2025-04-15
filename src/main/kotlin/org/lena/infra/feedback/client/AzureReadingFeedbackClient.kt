package org.lena.infra.feedback.client

import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import mu.KotlinLogging
import org.lena.api.dto.feedback.ai.reading.AiReadingFeedbackResponseDto
import org.lena.domain.feedback.client.AiFeedbackReadingClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AzureReadingFeedbackClient(
    @Value("\${azure.speech.key}") private val apiKey: String,
    @Value("\${azure.speech.region}") private val region: String
) : AiFeedbackReadingClient {

    private val logger = KotlinLogging.logger {}

    override fun speechToText(audioFilePath: String): String {
        val speechConfig = SpeechConfig.fromSubscription(apiKey, region)
        val audioConfig = AudioConfig.fromWavFileInput(audioFilePath)

        val recognizer = SpeechRecognizer(speechConfig, audioConfig)
        val result = recognizer.recognizeOnceAsync().get()

        logger.debug { "🟨 speechToText 정보 =====================================" }
        logger.debug { "Result ID         : ${result.resultId}" }
        logger.debug { "Reason            : ${result.reason}" }
        logger.debug { "Text              : ${result.text}" }
        logger.debug { "Duration          : ${result.duration}" }
        logger.debug { "Offset            : ${result.offset}" }
        logger.debug { "Json Result       : ${result.properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult)}" }
        logger.debug { "Cancellation Reason: ${(result as? CancellationDetails)?.reason}" }
        logger.debug { "Error Details     : ${(result as? CancellationDetails)?.errorDetails}" }
        logger.debug { "==============================================================" }


        if (result.reason == ResultReason.RecognizedSpeech) {
            return result.text // 음성에서 추출된 텍스트 반환
        } else {
            throw Exception("음성 인식 실패: ${result.reason}")
        }
    }


    override fun evaluatePronunciation(audioFilePath: String, sentenceFromFile: String, sentence: String?): AiReadingFeedbackResponseDto {
        val speechConfig = SpeechConfig.fromSubscription(apiKey, region)
        val audioConfig = AudioConfig.fromWavFileInput(audioFilePath)

        val referenceText = if (sentence == null) {
            sentenceFromFile
        } else{
            sentence
        }

        val pronunciationConfig = PronunciationAssessmentConfig(
            referenceText,
            PronunciationAssessmentGradingSystem.HundredMark,
            PronunciationAssessmentGranularity.Phoneme,
            false
        )

        val recognizer = SpeechRecognizer(speechConfig, audioConfig)
        pronunciationConfig.applyTo(recognizer)

        val result = recognizer.recognizeOnceAsync().get()

        logger.debug { "🟨 evaluatePronunciation 정보 =====================================" }
        logger.debug { "Result ID         : ${result.resultId}" }
        logger.debug { "Reason            : ${result.reason}" }
        logger.debug { "Text              : ${result.text}" }
        logger.debug { "Duration          : ${result.duration}" }
        logger.debug { "Offset            : ${result.offset}" }
        logger.debug { "Json Result       : ${result.properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult)}" }
        logger.debug { "Cancellation Reason: ${(result as? CancellationDetails)?.reason}" }
        logger.debug { "Error Details     : ${(result as? CancellationDetails)?.errorDetails}" }
        logger.debug { "==============================================================" }


        val json = result.properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult)
        logger.debug { "🟨 evaluatePronunciation json =====================================: $json" }


        return AiReadingFeedbackResponseDto.from(result, sentenceFromFile, sentence)
    }

}
