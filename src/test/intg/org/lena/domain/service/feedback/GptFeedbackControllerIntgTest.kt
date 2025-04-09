package org.lena.domain.service.feedback

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.GptFeedbackRequestDto
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Transactional
class GptFeedbackControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var imageRepository: ImageRepository

    private lateinit var testUser: User
    private lateinit var testImage: Image

    @BeforeEach
    fun setup() {
        testUser = userRepository.save(User(email = "test@example.com", name = "테스트유저"))
        testImage = imageRepository.save(
            Image(
                name = "샘플 이미지",
                path = "/images/sample.jpg",
                description = "샘플 설명",
                categoryId = 1
            )
        )
    }

    @Test
    @DisplayName("GPT 피드백 생성 성공")
    fun generateFeedback_피드백_생성_성공() {
        val request = GptFeedbackRequestDto(
            sentence = "This is a test sentence.",
            imageId = testImage.id!!
        )

        val result = webTestClient.post()
            .uri("/api/v1/ai/feedback/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("GPT 피드백 생성 결과: $result")
        assertNotNull(result)
        assertEquals(200, result.status)
        val data = result.data as LinkedHashMap<*, *>
        assertNotNull(data["correction"])
        assertNotNull(data["feedback"])
    }

    @Test
    @DisplayName("GPT 피드백 저장 성공")
    fun submitFeedback_피드백_저장_성공() {
        val request = GptFeedbackRequestDto(
            sentence = "Another feedback to save.",
            imageId = testImage.id!!
        )

        val result = webTestClient.post()
            .uri("/api/v1/ai/feedback/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("GPT 피드백 저장 결과: $result")
        assertNotNull(result)
        assertEquals(201, result.status)
    }

    @Test
    @DisplayName("GPT 피드백 생성 실패 - 문장 짧음 (유효성 검사)")
    fun generateFeedback_문장_짧으면_검증_오류() {
        val request = GptFeedbackRequestDto(
            sentence = "Hi",
            imageId = testImage.id!!
        )

        val result = webTestClient.post()
            .uri("/api/v1/ai/feedback/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("검증 실패 응답: $result")
        assertEquals(400, result?.status)
        assertTrue(result?.message?.contains("문장은 최소 5자 이상이어야") == true)
    }
}
