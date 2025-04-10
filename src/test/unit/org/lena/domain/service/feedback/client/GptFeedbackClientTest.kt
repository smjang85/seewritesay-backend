package org.lena.domain.service.feedback.client


import org.junit.jupiter.api.Test
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.infra.feedback.client.GptFeedbackClient

class GptFeedbackClientTest {

    private val client = GptFeedbackClient(
        apiKey = System.getenv("OPENAI_API_KEY") ?: "dummy-key",
        apiUrl = "https://api.openai.com/v1/chat/completions"
    )

    private val imageDesc = "A little girl in a green dress is walking along a winding dirt path through a meadow filled with white daisies. She is holding a large yellow umbrella while light rain falls gently from the sky. The scene is peaceful and bright despite the rain, with soft clouds and a big tree in the background."

    val testSentences = listOf(
        "girl walk",
        "girl walk umbrella park",
        "a girl walking with umbrella in the park",
        "A little girl is walking in the park",
        "A girl walks with an yellow umbrella",
        "A little girl is walking in the park with an umbrella.",
        "A little girl is walking along a path in the park, holding a yellow umbrella while it rains."
    )

    @Test
    fun `피드백 자동화 테스트`() {
        testSentences.forEach { sentence ->
            val result = client.getFeedback(
                sentence = sentence,
                imageDesc = "A little girl is walking along a winding path in a park, holding a yellow umbrella while it gently rains. She is wearing a green dress and brown shoes, and white daisies are blooming on both sides of the path. The sky is cloudy, and a large tree stands in the background."
            )
            println("입력: $sentence")
            println("등급: ${result.grade}")
            println("수정: ${result.correction}")
            println("피드백: ${result.feedback}")
            println("---------------------------")
        }
    }
}
