package org.lena.domain.service.feedback


import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.config.security.CustomUserPrincipal
import org.lena.infra.feedback.GptFeedbackServiceImpl
import org.lena.infra.feedback.client.GptFeedbackClient
import org.lena.domain.image.service.ImageService
import kotlin.test.assertEquals
import org.lena.api.dto.feedback.GptFeedbackResponseDto

class GptFeedbackServiceUnitTest {

    private lateinit var gptFeedbackService: GptFeedbackServiceImpl
    private val gptFeedbackClient: GptFeedbackClient = mockk()
    private val imageService: ImageService = mockk()

    private val dummyUser = CustomUserPrincipal(id = 1L, email = "test@lena.org", name = "TestUser")

    @BeforeEach
    fun setUp() {
        gptFeedbackService = GptFeedbackServiceImpl(
            gptFeedbackClient = gptFeedbackClient,
            imageService = imageService
        )
    }

    @Test
    @DisplayName("generateFeedback - GPT 응답에서 Correction, Feedback 추출 성공")
    fun generateFeedback_정상추출_성공() {
        // given
        val sentence = "This is test."
        val imageId = 100L
        val imageDescription = "A boy playing in the park"
        val gptMockResponse = GptFeedbackResponseDto(
            correction = "This is a test.",
            feedback = "Great try! Just remember to use 'a' before test.",
            grade = "B"
        )

        every { imageService.getDescriptionByImageId(imageId) } returns imageDescription
        every { gptFeedbackClient.getFeedback(sentence, imageDescription) } returns gptMockResponse

        // when
        val result = gptFeedbackService.generateFeedback(dummyUser, sentence, imageId)

        // then
        assertEquals("This is a test.", result.correction)
        assertEquals("Great try! Just remember to use 'a' before test.", result.feedback)
        assertEquals("B", result.grade)
    }


    @Test
    @DisplayName("generateFeedback - GPT 응답이 포맷을 따르지 않을 경우 기본값 반환")
    fun generateFeedback_포맷없음_기본값반환() {
        val sentence = "This is weird."
        val imageId = 101L
        val imageDescription = "An empty beach"
        val fallbackResponse = GptFeedbackResponseDto(
            correction = sentence,
            feedback = "",
            grade = "F"
        )

        every { imageService.getDescriptionByImageId(imageId) } returns imageDescription
        every { gptFeedbackClient.getFeedback(sentence, imageDescription) } returns fallbackResponse

        val result = gptFeedbackService.generateFeedback(dummyUser, sentence, imageId)

        assertEquals(sentence, result.correction)
        assertEquals("", result.feedback)
        assertEquals("F", result.grade)
    }
}
