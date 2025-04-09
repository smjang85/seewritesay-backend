package org.lena.domain.service.auth

import org.lena.domain.auth.JwtTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.infra.auth.JwtTokenServiceImpl
import org.lena.domain.user.entity.User
import java.util.Base64
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JwtTokenServiceImplTest {

    private lateinit var jwtTokenService: JwtTokenServiceImpl

    private val secret = Base64.getEncoder().encodeToString("super-secret-key-12345678901234567890".toByteArray())
    private val validityMs = 1000L * 60 * 5  // 5분

    @BeforeEach
    fun setUp() {
        jwtTokenService = JwtTokenServiceImpl()
        jwtTokenService.apply {
            this::class.java.getDeclaredField("secretKeyString").apply {
                isAccessible = true
                set(this@apply, secret)
            }
            this::class.java.getDeclaredField("validityInMilliseconds").apply {
                isAccessible = true
                set(this@apply, validityMs)
            }
        }
    }

    @Test
    fun `createToken - 토큰 생성 및 파싱 성공`() {
        // given
        val user = User.of(email = "test@lena.org", name = "Tester")

        // when
        val token = jwtTokenService.createToken(user)

        // then
        assertNotNull(token)
        assertEquals(user.email, jwtTokenService.extractEmail(token))
        assertEquals(user.name, jwtTokenService.extractName(token))
    }

    @Test
    fun `extractId - 토큰에서 id 추출 성공`() {
        val user = User.of(email = "test@lena.org", name = "Tester")
        val token = jwtTokenService.createToken(user)

        val extractedId = jwtTokenService.extractId(token)
        assertEquals(user.id, extractedId)
    }
}
