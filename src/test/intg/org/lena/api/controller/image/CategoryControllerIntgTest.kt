package org.lena.api.controller.image

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.image.CategoryRequestDto
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    fun createCategory_카테고리생성성공() {
        // given
        val request = CategoryRequestDto(name = "자연")

        // when
        val response = webTestClient.post()
            .uri("/api/v1/images/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        // then
        println("✅ 카테고리 생성 응답: $response")
        assertNotNull(response)
    }

    @Test
    fun getAllCategories_전체조회성공() {
        // given
        categoryRepository.saveAll(
            listOf(
                Category.of(name = "가족"),
                Category.of(name = "여행")
            )
        )

        // when
        val response = webTestClient.get()
            .uri("/api/v1/images/categories")
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        // then
        println("📂 카테고리 전체 조회 결과: $response")
        assertNotNull(response)
    }
}
