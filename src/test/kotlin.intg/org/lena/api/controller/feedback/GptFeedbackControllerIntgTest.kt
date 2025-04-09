package org.lena.api.controller.feedback

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.GptFeedbackRequestDto
import org.lena.api.dto.feedback.GptFeedbackResponseDto
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
            Image(
                name = "샘플 이미지",
                path = "/images/sample.jpg",
                description = "샘플 설명",
                categoryId = 1
            )
        )
    }

    @Test
    fun `GPT 피드백 생성 요청 - 성공`() {
        // given
        val request = GptFeedbackRequestDto(
            sentence = "This is a test sentence.",
            imageId = testImage.id!!
        )

        // when
        val result = webTestClient.post()
            .uri("/api/v1/ai/feedback/generate")
            .headers { it.setBasicAuth(testUser.email, "dummy") } // 실제 JWT 처리 시 대체 필요
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("✅ GPT 생성 결과: $result")
    }

    @Test
    fun `GPT 피드백 저장 요청 - 성공`() {
        // given
        val request = GptFeedbackRequestDto(
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
    fun `인증 없는 요청 - 실패`() {
        val request = GptFeedbackRequestDto(
            sentence = "Should fail",
            imageId = testImage.id!!
        )

        webTestClient.post()
            .uri("/api/v1/ai/feedback/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .consumeWith {
                println("❌ 인증 실패 테스트 성공")
            }
    }
}
