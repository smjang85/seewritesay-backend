package org.lena.api.controller.auth

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.domain.auth.JwtTokenService
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AuthControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jwtTokenService: JwtTokenService

    private lateinit var testUser: User
    private lateinit var validToken: String

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
        testUser = userRepository.save(User.of(email = "auth@test.com", name = "AuthTester"))
        validToken = jwtTokenService.createToken(testUser)
    }

    @Test
    fun refreshToken_토큰갱신_성공() {
        val response = webTestClient.post()
            .uri("/api/v1/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $validToken")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        println("✅ 새 토큰: $response")
        assertNotNull(response)
    }


    @Test
    fun refreshToken_만료토큰_실패() {
        val expiredToken = jwtTokenService.createToken(testUser)
        Thread.sleep(2000) // 2초 대기 → validity-ms: 1000ms로 설정된 test 환경 기준

        val response = webTestClient.post()
            .uri("/api/v1/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $expiredToken")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        println("❌ 만료 토큰 응답: $response")
    }
}
