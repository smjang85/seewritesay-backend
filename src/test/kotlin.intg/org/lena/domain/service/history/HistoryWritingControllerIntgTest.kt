package org.lena.domain.service.history

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
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
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Transactional
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
        testUser = userRepository.save(User.of(email = "test@lena.org", name = "TestUser"))
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
    @DisplayName("saveHistory_작문 히스토리 저장 성공")
    fun saveHistory_작문_히스토리_저장_성공() {
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

        println("히스토리 저장 응답: $response")
        assertNotNull(response)
        assertEquals(201, response.status)
    }

    @Test
    @DisplayName("getHistory_전체 작문 히스토리 조회 성공")
    fun getHistory_전체_조회_성공() {
        // given
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

        // when
        val response = webTestClient.get()
            .uri("/api/v1/history/writing")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("전체 히스토리 조회 결과: $response")
        assertNotNull(response)
        assertEquals(200, response.status)
    }

    @Test
    @DisplayName("getHistoryWithCategory_카테고리별 작문 히스토리 조회 성공")
    fun getHistoryWithCategory_카테고리별_조회_성공() {
        val response = webTestClient.get()
            .uri("/api/v1/history/writing/with-category")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("카테고리별 히스토리 응답: $response")
        assertNotNull(response)
        assertEquals(200, response.status)
    }
}
