package org.lena.domain.service.feedback

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.api.dto.feedback.ai.writing.AiWritingFeedbackResponseDto
import org.lena.domain.image.service.ImageService
import org.lena.infra.feedback.AiFeedbackServiceImpl
import org.lena.infra.feedback.client.GptWritingFeedbackClient
import kotlin.test.assertEquals

class GptFeedbackServiceUnitTest {

    private lateinit var gptFeedbackService: AiFeedbackServiceImpl
    private val gptWritingFeedbackClient: GptWritingFeedbackClient = mockk()
    private val imageService: ImageService = mockk()

    @BeforeEach
    fun setup() {
        gptFeedbackService = AiFeedbackServiceImpl(
            gptWritingFeedbackClient = gptWritingFeedbackClient,
            imageService = imageService,
            azureReadingFeedbackClient = TODO()
        )
    }

    @Test
    @DisplayName("generateFeedback_정상응답_성공")
    fun generateFeedback_정상응답_성공() {
        // given
        val sentence = "This is test."
        val imageId = 1L
        val imageDescription = "A boy is jumping with a ball."

        val mockResponse = AiWritingFeedbackResponseDto(
            correction = "This is a test.",
            feedback = "Try adding an article before 'test'.",
            grade = "B+"
        )

        every { imageService.getDescriptionByImageId(imageId) } returns imageDescription
        every { gptWritingFeedbackClient.generateWritingFeedback(sentence, imageDescription) } returns mockResponse

        // when
        val result = gptFeedbackService.generateWritingFeedback(sentence, imageId)

        // then
        assertEquals("This is a test.", result.correction)
        assertEquals("Try adding an article before 'test'.", result.feedback)
        assertEquals("B+", result.grade)
    }

    @Test
    @DisplayName("generateFeedback_형식이상_기본값반환")
    fun generateFeedback_형식이상_기본값반환() {
        // given
        val sentence = "Unclear sentence"
        val imageId = 2L
        val imageDescription = "A foggy road in winter."

        val fallbackResponse = AiWritingFeedbackResponseDto(
            correction = sentence,
            feedback = "",
            grade = "F"
        )

        every { imageService.getDescriptionByImageId(imageId) } returns imageDescription
        every { gptWritingFeedbackClient.generateWritingFeedback(sentence, imageDescription) } returns fallbackResponse

        // when
        val result = gptFeedbackService.generateWritingFeedback(sentence, imageId)

        // then
        assertEquals("Unclear sentence", result.correction)
        assertEquals("", result.feedback)
        assertEquals("F", result.grade)
    }
}
