// 파일 위치: org.lena.infra.feedback.client.GptFeedbackClient.kt
package org.lena.infra.feedback.client

import mu.KotlinLogging
import org.lena.domain.feedback.client.AiFeedbackClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GptFeedbackClient(
    @Value("\${openai.api.key}") private val apiKey: String,
    @Value("\${openai.api.url}") private val apiUrl: String
) : AiFeedbackClient {

    private val logger = KotlinLogging.logger {}

    private val webClient = WebClient.builder()
        .baseUrl(apiUrl)
        .defaultHeader("Authorization", "Bearer $apiKey")
        .build()

    override fun getFeedback(sentence: String, imageDesc: String): String {
        val prompt = """
            너는 영어 선생님입니다.
            
            이미지 설명 : "$imageDesc"
            사용자 문장: "$sentence"
            
            사용자가 쓴 문장만 첨삭해주세요. 아래 문장을 첨삭해 주세요.
            
            1. 문장을 자연스럽고 문법적으로 맞게 수정하세요.
            2. 수정한 이유를 한국어로 상세히 설명해 주세요.
            3. 반드시 아래 형식을 지켜 주세요:
            
            Correction: 수정된 문장
            Feedback: 한국어 피드백 설명
        """.trimIndent()

        val request = mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "당신은 친절하고 이해하기 쉬운 영어 교사입니다."),
                mapOf("role" to "user", "content" to prompt)
            )
        )

        val response = webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        val choices = response?.get("choices") as? List<Map<String, Any>> ?: return "피드백을 받을 수 없습니다."
        val message = choices.firstOrNull()?.get("message") as? Map<String, String>
        return message?.get("content") ?: "피드백이 없습니다."
    }
}
