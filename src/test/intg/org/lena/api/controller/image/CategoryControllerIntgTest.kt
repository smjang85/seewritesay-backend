package org.lena.api.controller.image

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.image.CategoryResponseDto
import org.lena.domain.image.entity.Category
import org.lena.domain.image.repository.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CategoryControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setup() {
        categoryRepository.deleteAll()
    }

    @Test
    fun `카테고리 생성 성공`() {
        val request = CategoryResponseDto(id = null, name = "여행")

        val response = webTestClient.post()
            .uri("/api/v1/images/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("✅ 생성된 카테고리 응답: $response")
        assertNotNull(response)
    }

    @Test
    fun `카테고리 전체 조회 성공`() {
        categoryRepository.saveAll(
            listOf(
                Category.of(name = "학교"),
                Category.of(name = "가족")
            )
        )

        val response = webTestClient.get()
            .uri("/api/v1/images/categories")
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("📂 전체 카테고리 조회 응답: $response")
        assertNotNull(response)
    }
}
