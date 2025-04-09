package org.lena.domain.service.user

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
class UserSettingControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var userRepository: UserRepository

    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
        testUser = userRepository.save(User(email = "test@lena.org", name = "테스트유저"))
    }

    @Test
    @DisplayName("getUserSettings_사용자 설정 조회 성공")
    fun getUserSettings_사용자_설정_조회_성공() {
        val response = webTestClient.get()
            .uri("/api/v1/user/settings")
            .headers { it.setBasicAuth(testUser.email, "dummy") }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(Map::class.java)
            .returnResult()
            .responseBody

        assertNotNull(response)
        assertEquals("테스트유저", response?.get("name"))
    }
}
