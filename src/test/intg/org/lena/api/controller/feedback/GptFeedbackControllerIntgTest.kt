package org.lena.api.controller.feedback

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.ai.writing.AiWritingFeedbackRequestDto
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
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
        userRepository.deleteAll()
        imageRepository.deleteAll()

        testUser = userRepository.save(User.of(email = "test@example.com", name = "테스트유저"))
        testImage = imageRepository.save(
            Image.of(
                name = "샘플 이미지",
                path = "/images/sample.jpg",
                categoryId = 1,
                description = "샘플 설명"
            )
        )
    }

    @Test
    fun generateFeedback_피드백_생성_성공() {
        // given
        val request = AiWritingFeedbackRequestDto(
            sentence = "This is a test sentence.",
            imageId = testImage.id!!
        )

        // when
        val result = webTestClient.post()
            .uri("/api/v1/ai/feedback/generate")
            .headers { it.setBasicAuth(testUser.email, "dummy") } // 실제 JWT 인증으로 교체 필요
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("✅ GPT 피드백 생성 결과: $result")
    }

    @Test
    fun submitFeedback_피드백_저장_성공() {
        // given
        val request = AiWritingFeedbackRequestDto(
            sentence = "Another feedback to save.",
            imageId = testImage.id!!
        )

        // when
        webTestClient.post()
            .uri("/api/v1/ai/feedback/submit")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .consumeWith {
                println("✅ 피드백 저장 완료")
            }
    }

    @Test
    fun generateFeedback_인증_없음_실패() {
        // given
        val request = AiWritingFeedbackRequestDto(
            sentence = "Should fail",
            imageId = testImage.id!!
        )

        // when
        webTestClient.post()
            .uri("/api/v1/ai/feedback/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .consumeWith {
                println("❌ 인증 실패 정상 처리됨")
            }
    }
}
