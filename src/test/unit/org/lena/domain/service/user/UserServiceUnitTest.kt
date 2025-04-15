package org.lena.domain.service.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.lena.infra.user.UserServiceImpl
import java.util.*
import kotlin.test.*

class UserServiceUnitTest {

    private lateinit var userService: UserServiceImpl
    private val userRepository: UserRepository = mockk()

    private val dummyUser = User.of(email = "test@lena.org", name = "TestUser")

    @BeforeEach
    fun setup() {
        userService = UserServiceImpl(userRepository)
    }

    @Test
    @DisplayName("registerOrUpdate_기존유저_업데이트")
    fun registerOrUpdate_기존유저_업데이트() {
        every { userRepository.findByEmail("test@lena.org") } returns dummyUser
        every { userRepository.save(any()) } returns dummyUser

        val result = userService.registerOrUpdate("test@lena.org", "TestUser")

        assertEquals("test@lena.org", result.email)
        assertNotNull(result.lastLoginAt)
    }

    @Test
    @DisplayName("registerOrUpdate_신규등록")
    fun registerOrUpdate_신규등록() {
        every { userRepository.findByEmail("new@lena.org") } returns null
        val slotUser = slot<User>()
        every { userRepository.save(capture(slotUser)) } answers { slotUser.captured }

        val result = userService.registerOrUpdate("new@lena.org", "NewUser")

        assertEquals("new@lena.org", result.email)
        assertEquals("NewUser", result.name)
        assertNotNull(result.lastLoginAt)
    }

    @Test
    @DisplayName("findById_조회성공")
    fun findById_조회성공() {
        every { userRepository.findById(1L) } returns Optional.of(dummyUser)

        val result = userService.findById(1L)

        assertEquals(dummyUser.email, result.email)
    }

    @Test
    @DisplayName("findById_조회실패_예외")
    fun findById_조회실패_예외() {
        every { userRepository.findById(99L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalArgumentException> {
            userService.findById(99L)
        }

        assertTrue(exception.message!!.contains("User not found"))
    }
}
