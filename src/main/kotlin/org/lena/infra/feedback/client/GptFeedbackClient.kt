// 파일 위치: org.lena.infra.feedback.client.GptFeedbackClient.kt
package org.lena.infra.feedback.client

import mu.KotlinLogging
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.domain.feedback.client.AiFeedbackClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Component
class GptFeedbackClient(
    @Value("\${openai.api.key}") private val apiKey: String,
    @Value("\${openai.api.url}") private val apiUrl: String
) : AiFeedbackClient {

    private val logger = KotlinLogging.logger {}

    private val webClient = WebClient.builder()
        .baseUrl(apiUrl)
        .defaultHeader("Authorization", "Bearer $apiKey")
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient.create()
                    .responseTimeout(Duration.ofSeconds(20)) // 💡 여기를 추가
            )
        )
        .build()

    override fun getFeedback(sentence: String, imageDesc: String): GptFeedbackResponseDto {
        if (!Regex("[a-zA-Z]").containsMatchIn(sentence)) {
            return GptFeedbackResponseDto("없음", "영어 문장으로 작성해 주세요.", "F")
        }

        val prompt = """
너는 영어 작문을 평가하는 교사입니다. 아래 사진 설명과 사용자 문장을 비교하고, 작문 능력을 평가하세요.

[사진 설명]
"$imageDesc"

[사용자 문장]
"$sentence"

[평가 기준]
1. 사용자 문장이 영어가 아니거나, 의미 없는 문자(Gibberish 또는 Random characters)일 경우에만 → Grade: F
   - 예: vjvjvjv, asdfasdf, xcvbnm, zzzz, 등 의미없는 영문자의 반복
   
2. 사용자 문장이 매끄럽고, 사진 설명과 잘 어울리며, 문장이 완성되고 묘사나 어휘가 다양한 경우 → Grade: A+
3. 사용자 문장이 매끄럽고, 사진 설명과 잘 어울리며, 문장이 완성된 경우 → Grade: A
   (※ 단, 배경만 있는 문장은 A 부여 불가. 인물·행동 등 추가 설명이 포함되어야 함. 배경 사진 설명은 예외)
4. 사진 설명과 일부라도 일치하며, 사용자 문장이 완성되었거나 이해 가능한 경우 → Grade: B
5. 사진 설명과 일부라도 일치하지만, 사용자 문장이 대부분 완성되지 않은 경우 → Grade: C
6. 사진 설명과 거의 관련 없거나, 일치하지 않고 문장이 대부분 완성되지 않은 경우 → Grade: D

[출력 형식]
Sentence: 사용자가 입력한 문장
Correction: A+를 받을 수 있는 문장을 제안 (없으면 "없음")
Feedback: 피드백 문장 (한국어)
Grade: A+,A,B,C,D,F 중 하나

[주의 사항]
- 의미 없는 랜덤 문자나 알파벳 반복(Gibberish 예시: vjvjvjvjv, asdfasdf, xcvbnm, qweqwe, zzzz 등)은 반드시 Grade: F로 평가해야 합니다.
- Gibberish는 절대 A~D 등급을 부여하지 마세요.
- 문장이 아무리 짧더라도, 의미가 전달되지 않으면 Grade: F입니다.
- 사용자 문장이 정상적일 경우에는 다소 후하게 평가해도 괜찮습니다.
- 사용자 문장을 임의로 보완하거나 재해석하지 마세요.
- Correction은 A+ 수준의 예시만 출력하며, 수정 제안이 없는 경우 "없음"이라고만 작성하세요.
- Grade F 는 절대 Correction을 생성하지 마세요. (Correction: 없음)
- Grade F 가 아닌 경우 반드시 Correction 문장을 주세요. ( GRADE B,C,D 인경우 반드시 Correction 을 생성하세요)
- 출력 형식을 반드시 지키고, 추가 설명을 붙이지 마세요.
""".trimIndent()




        val request = mapOf(
            "model" to "gpt-3.5-turbo",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "당신은 친절하지만 기준이 엄격한 영어 작문 교사입니다."),
                mapOf("role" to "user", "content" to prompt)
            )
        )

        val response = webClient.post()
            .bodyValue(request)
            .retrieve()

            .bodyToMono(Map::class.java)
            .block()

        val choices = response?.get("choices") as? List<Map<String, Any>> ?: error("GPT 응답 오류")
        val message = choices.firstOrNull()?.get("message") as? Map<String, String>
        val content = message?.get("content") ?: error("GPT 응답 형식 오류")

        val correction = Regex("Correction:\\s*(.*?)(?:\\n|$)", RegexOption.DOT_MATCHES_ALL)
            .find(content)?.groupValues?.get(1)?.trim() ?: ""
        val feedback = Regex("Feedback:\\s*(.*?)(?:\\n|$)", RegexOption.DOT_MATCHES_ALL)
            .find(content)?.groupValues?.get(1)?.trim() ?: ""
        val gradeRaw = Regex("Grade:\\s*(.*)").find(content)?.groupValues?.get(1)?.trim() ?: ""
        val validGrades = setOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F")
        val grade = if (gradeRaw in validGrades) gradeRaw else "F"

        logger.debug("response : $response")
        logger.debug("imageDesc : $imageDesc")
        logger.debug("sentence : $sentence")
        logger.debug("correction : $correction")
        logger.debug("feedback : $feedback")
        logger.debug("grade : $grade")

        return GptFeedbackResponseDto(
            correction = correction,
            feedback = feedback,
            grade = grade
        )
    }
}
