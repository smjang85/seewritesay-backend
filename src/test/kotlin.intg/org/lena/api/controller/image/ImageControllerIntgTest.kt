package org.lena.api.controller.image

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
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
class ImageControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var imageRepository: ImageRepository

    @BeforeEach
    fun setup() {
        imageRepository.deleteAll()
        imageRepository.saveAll(
            listOf(
                Image(name = "이미지1", path = "/images/1.jpg", description = "설명1", categoryId = 1),
                Image(name = "이미지2", path = "/images/2.jpg", description = "설명2", categoryId = 1)
            )
        )
    }

    @Test
    fun `전체 이미지 목록 조회 성공`() {
        val response = webTestClient.get()
            .uri("/api/v1/images")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiResponse::class.java)
            .returnResult()
            .responseBody

        println("✅ 이미지 목록 응답: $response")
        assertNotNull(response)
    }
}
