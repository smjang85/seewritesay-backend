package org.lena.domain.service.feedback.client

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.lena.infra.feedback.client.GptWritingFeedbackClient

class GptWritingFeedbackClientTest {

    companion object {
        private lateinit var client: GptWritingFeedbackClient
        private const val imageDesc = """
            A little girl in a green dress is walking along a winding dirt path through a meadow filled with white daisies. 
            She is holding a large yellow umbrella while light rain falls gently from the sky. 
            The scene is peaceful and bright despite the rain, with soft clouds and a big tree in the background.
        """

        @JvmStatic
        @BeforeAll
        fun setup() {
            val apiKey = System.getenv("OPENAI_API_KEY")
            require(!apiKey.isNullOrBlank()) {
                "✅ 테스트를 위해 OPENAI_API_KEY 환경변수가 설정되어 있어야 합니다."
            }

            client = GptWritingFeedbackClient(
                apiKey = apiKey,
                apiUrl = "https://api.openai.com/v1/chat/completions"
            )
        }
    }

    private val testSentences = listOf(
        "girl walk",
        "girl walk umbrella park",
        "a girl walking with umbrella in the park",
        "A little girl is walking in the park",
        "A girl walks with an yellow umbrella",
        "A little girl is walking in the park with an umbrella.",
        "A little girl is walking along a path in the park, holding a yellow umbrella while it rains."
    )

    @Test
    fun `GPT 피드백 실제 응답 테스트`() {
        testSentences.forEach { sentence ->
            println("\n▶ 입력 문장: $sentence")

            val result = client.generateWritingFeedback(sentence, imageDesc)

            println("🟡 Grade: ${result.grade}")
            println("🟢 Correction: ${result.correction}")
            println("📝 Feedback: ${result.feedback}")
            println("===========================================")

            // 선택적으로 등급 검증도 가능 (예: "F"는 아니어야 함)
            assert(result.grade in setOf("A+", "A", "B", "C", "D", "F")) {
                "유효하지 않은 등급: ${result.grade}"
            }
        }
    }
}
