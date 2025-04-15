package org.lena.domain.service.auth

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.user.entity.User
import org.lena.infra.auth.JwtTokenServiceImpl
import org.lena.support.BaseTestUtils.setId
import java.util.Base64
import kotlin.test.*

class JwtTokenServiceImplTest {

    private lateinit var jwtTokenService: JwtTokenServiceImpl

    private val secret = Base64.getEncoder()
        .encodeToString("super-secret-key-12345678901234567890".toByteArray())
    private val validityMs = 1000L * 60 * 10 // 10분

    @BeforeEach
    fun setup() {
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
    @DisplayName("createToken_정상생성_및_파싱")
    fun createToken_정상생성_및_파싱() {
        // given
        val user = User.of(email = "tester@lena.org", name = "Tester")
        setId(user, 123L)

        // when
        val token = jwtTokenService.createToken(user)

        // then
        assertNotNull(token)
        assertEquals("tester@lena.org", jwtTokenService.extractEmail(token))
        assertEquals("Tester", jwtTokenService.extractName(token))
        assertEquals(123L, jwtTokenService.extractId(token))
    }

    @Test
    @DisplayName("isTokenExpired_정상토큰이면_false")
    fun isTokenExpired_정상토큰이면_false() {
        val user = User.of(email = "active@lena.org", name = "ActiveUser")
        setId(user, 100L)

        val token = jwtTokenService.createToken(user)
        val result = jwtTokenService.isTokenExpired(token)

        assertFalse(result)
    }

    @Test
    @DisplayName("isTokenExpired_만료된_토큰이면_true")
    fun isTokenExpired_만료된_토큰이면_true() {
        val user = User.of(email = "expired@lena.org", name = "ExpiredUser")
        setId(user, 999L)

        val shortLivedJwt = JwtTokenServiceImpl().apply {
            javaClass.getDeclaredField("secretKeyString").apply {
                isAccessible = true
                set(this@apply, secret)
            }
            javaClass.getDeclaredField("validityInMilliseconds").apply {
                isAccessible = true
                set(this@apply, 1L)
            }
        }

        val token = shortLivedJwt.createToken(user)

        Thread.sleep(10) // 최소한의 만료 유도
        assertTrue(shortLivedJwt.isTokenExpired(token))
    }

    @Test
    @DisplayName("refreshToken_토큰_재발급_성공")
    fun refreshToken_토큰_재발급_성공() {
        val user = User.of(email = "refresher@lena.org", name = "Refresher")
        setId(user, 888L)

        val token = jwtTokenService.refreshToken(user)

        assertNotNull(token)
        assertEquals("refresher@lena.org", jwtTokenService.extractEmail(token))
    }
}
