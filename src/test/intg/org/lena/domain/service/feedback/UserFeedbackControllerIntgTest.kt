package org.lena.domain.service.feedback

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.UserFeedbackRequestDto
import org.lena.api.dto.feedback.UserFeedbackResetRequestDto
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
class UserFeedbackControllerIntgTest {

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
        testUser = userRepository.save(User(email = "test@lena.org", name = "TestUser"))
        testImage = imageRepository.save(
            Image(
                name = "Test Image",
                path = "/images/test.jpg",
                description = "테스트 이미지 설명",
                categoryId = 1
            )
        )
    }

    @Test
    @DisplayName("getRemainingCount_남은 피드백 횟수 조회 성공")
    fun getRemainingCount_남은_피드백_조회() {
        val result = webTestClient.get()
            .uri("/api/v1/user/feedback?imageId=${testImage.id}")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("남은 횟수 조회 응답: $result")
        assertNotNull(result)
        assertEquals(200, result.status)
        assertTrue(result.data is Int)
    }

    @Test
    @DisplayName("decrementFeedback_피드백 횟수 차감 요청 성공")
    fun decrementFeedback_피드백_차감() {
        val request = UserFeedbackRequestDto(imageId = testImage.id!!)

        val response = webTestClient.post()
            .uri("/api/v1/user/feedback/decrement")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("횟수 차감 응답: $response")
        assertNotNull(response)
        assertEquals(200, response.status)
    }

    @Test
    @DisplayName("resetFeedback_피드백 횟수 초기화 요청 성공")
    fun resetFeedback_피드백_초기화() {
        val request = UserFeedbackResetRequestDto(
            imageId = testImage.id!!,
            count = 5
        )

        val response = webTestClient.post()
            .uri("/api/v1/user/feedback/reset")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("초기화 응답: $response")
        assertNotNull(response)
        assertEquals(200, response.status)
        assertEquals(5, response.data)
    }
}
