package org.lena.api.dto.feedback.reading

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.microsoft.cognitiveservices.speech.PropertyId
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult

data class AiReadingFeedbackResponseDto(
    val accuracyScore: Double,
    val fluencyScore: Double,
    val completenessScore: Double,
    val pronScore: Double,
    val confidence: Double,
    val sentenceFromFile: String,
    val sentence: String?,
) {
    companion object {
        fun from(result: SpeechRecognitionResult, sentenceFromFile: String, sentence: String?): AiReadingFeedbackResponseDto {
            val json = result.properties.getProperty(PropertyId.SpeechServiceResponse_JsonResult)
            val jsonNode = jacksonObjectMapper().readTree(json)
            val pronunciation = jsonNode["NBest"]?.get(0)?.get("PronunciationAssessment")

            val confidenceRaw = jsonNode["NBest"]?.get(0)?.get("Confidence")?.asDouble() ?: 0.0
            val confidenceRounded = String.format("%.2f", confidenceRaw).toDouble()

            return AiReadingFeedbackResponseDto(
                accuracyScore = pronunciation?.get("AccuracyScore")?.asDouble() ?: 0.0,
                fluencyScore = pronunciation?.get("FluencyScore")?.asDouble() ?: 0.0,
                completenessScore = pronunciation?.get("CompletenessScore")?.asDouble() ?: 0.0,
                pronScore = pronunciation?.get("PronScore")?.asDouble() ?: 0.0,
                confidence = confidenceRounded,
                sentenceFromFile = sentenceFromFile,
                sentence = sentence
            )
        }
    }
}