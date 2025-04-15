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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        testUser = userRepository.save(User.of(email = "test@lena.org", name = "테스트유저"))
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
    fun saveHistory_작문히스토리저장성공() {
        val request = HistoryWritingRequestDto(
            imageId = testImage.id!!,
            sentence = "This is a test sentence.",
            grade = "A"
        )

        val response = webTestClient.post()
            .uri("/api/v1/history/writing")
            .headers { it.setBearerAuth("mock-jwt-token") } // 실제 토큰이 필요한 경우 대체
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNoContent

        println("✅ 작문 히스토리 저장 완료")
    }

    @Test
    fun getHistory_전체조회성공() {
        val response = webTestClient.get()
            .uri("/api/v1/history/writing")
            .headers { it.setBearerAuth("mock-jwt-token") }
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("📚 전체 조회 응답: $response")
        assertNotNull(response)
    }

    @Test
    fun getHistoryWithCategory_카테고리조회성공() {
        val response = webTestClient.get()
            .uri("/api/v1/history/writing/with-category")
            .headers { it.setBearerAuth("mock-jwt-token") }
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("📂 카테고리별 조회 응답: $response")
        assertNotNull(response)
    }

    @Test
    fun deleteHistory_삭제성공() {
        // 먼저 저장
        val saved = webTestClient.post()
            .uri("/api/v1/history/writing")
            .headers { it.setBearerAuth("mock-jwt-token") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                HistoryWritingRequestDto(
                    imageId = testImage.id!!,
                    sentence = "Sentence to delete",
                    grade = "B"
                )
            )
            .exchange()
            .expectStatus().isNoContent

        // 실제 ID는 서비스에서 리턴하지 않으니, 이 테스트는 Controller 반환값 확장 필요 시 완성 가능

        println("🗑️ 삭제 테스트 준비 완료 (삭제 ID는 수동 확인 필요)")
    }
}
