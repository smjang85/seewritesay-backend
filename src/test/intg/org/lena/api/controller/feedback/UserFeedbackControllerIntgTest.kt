package org.lena.api.controller.feedback

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.user.UserFeedbackRequestDto
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
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
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
        userRepository.deleteAll()
        imageRepository.deleteAll()

        testUser = userRepository.save(User.of(email = "test@lena.org", name = "TestUser"))
        testImage = imageRepository.save(
            Image(
                name = "샘플 이미지",
                path = "/images/sample.jpg",
                description = "설명",
                categoryId = 1
            )
        )
    }

    @Test
    fun getRemainingCount_남은피드백조회_성공() {
        val request = UserFeedbackRequestDto(imageId = testImage.id!!)

        val response = webTestClient
            .post() // 👉 POST로 변경 (GET은 바디를 포함할 수 없음)
            .uri("/api/v1/user/feedback")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("✅ 남은 피드백 응답: $response")
        assertNotNull(response)
    }

    @Test
    fun decrementWritingFeedbackCount_작문피드백차감_성공() {
        val request = UserFeedbackRequestDto(imageId = testImage.id!!)

        webTestClient.post()
            .uri("/api/v1/user/feedback/writing/decrement")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNoContent

        println("✅ 피드백 차감 완료")
    }
}
