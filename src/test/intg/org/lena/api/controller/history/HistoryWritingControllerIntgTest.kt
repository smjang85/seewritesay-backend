package org.lena.api.controller.history

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.history.HistoryWritingRequestDto
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class HistoryWritingControllerIntgTest {

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

        testUser = userRepository.save(User(email = "test@lena.org", name = "TestUser"))
        testImage = imageRepository.save(
            Image(
                name = "테스트 이미지",
                path = "/images/test.jpg",
                description = "설명",
                categoryId = 1
            )
        )
    }

    @Test
    fun `작문 히스토리 저장 성공`() {
        val request = HistoryWritingRequestDto(
            imageId = testImage.id!!,
            sentence = "This is a test sentence."
        )

        val response = webTestClient.post()
            .uri("/api/v1/history/writing")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("✅ 히스토리 저장 응답: $response")
        assertNotNull(response)
    }

    @Test
    fun `작문 히스토리 전체 조회 성공`() {
        // 먼저 저장
        val request = HistoryWritingRequestDto(
            imageId = testImage.id!!,
            sentence = "Another test sentence."
        )

        webTestClient.post()
            .uri("/api/v1/history/writing")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated

        // 조회
        val response = webTestClient.get()
            .uri("/api/v1/history/writing")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("📜 히스토리 조회 결과: $response")
        assertNotNull(response)
    }

    @Test
    fun `카테고리별 작문 히스토리 조회 성공`() {
        val response = webTestClient.get()
            .uri("/api/v1/history/writing/with-category")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("📂 카테고리별 히스토리 응답: $response")
        assertNotNull(response)
    }
}
