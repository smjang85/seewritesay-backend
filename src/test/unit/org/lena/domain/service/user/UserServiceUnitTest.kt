package org.lena.domain.service.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.api.dto.user.UserSettingsResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.feedback.service.UserFeedbackService
import org.lena.domain.image.entity.Image
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.lena.infra.user.UserServiceImpl
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

class UserServiceUnitTest {

    private lateinit var userService: UserServiceImpl
    private val userRepository: UserRepository = mockk()
    private val userFeedbackService: UserFeedbackService = mockk()
    private val imageService: ImageService = mockk()

    private val dummyUser = User.of(email = "test@lena.org", name = "TestUser")
    private val dummyImage = Image.of(name = "샘플", path = "/test.jpg", categoryId = 1, description = "desc")

    @BeforeEach
    fun setup() {
        userService = UserServiceImpl(
            userRepository = userRepository,
            userFeedbackService = userFeedbackService,
            imageService = imageService
        )
    }

    @Test
    @DisplayName("registerOrUpdate - 기존 유저 업데이트")
    fun registerOrUpdate_기존유저_업데이트() {
        every { userRepository.findByEmail("test@lena.org") } returns dummyUser
        every { userRepository.save(any()) } returns dummyUser

        val result = userService.registerOrUpdate("test@lena.org", "TestUser")

        assertEquals("test@lena.org", result.email)
        assertNotNull(result.lastLoginAt)
    }

    @Test
    @DisplayName("registerOrUpdate - 신규 유저 등록")
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
    @DisplayName("findById - 사용자 ID로 조회 성공")
    fun findById_조회성공() {
        every { userRepository.findById(1L) } returns Optional.of(dummyUser)

        val result = userService.findById(1L)

        assertEquals(dummyUser.email, result.email)
    }

    @Test
    @DisplayName("findById - ID로 조회 실패시 예외 발생")
    fun findById_조회실패_예외() {
        every { userRepository.findById(99L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalArgumentException> {
            userService.findById(99L)
        }

        assertTrue(exception.message!!.contains("User not found"))
    }

    @Test
    @DisplayName("getUserSettings - 사용자 설정 반환 성공")
    fun getUserSettings_성공() {
        val principal = CustomUserPrincipal(id = 1L, email = "test@lena.org", name = "테스트유저")

        every { userRepository.findById(1L) } returns Optional.of(dummyUser)
        every { imageService.findById(1L) } returns dummyImage
        every { userFeedbackService.getRemainingCount(dummyUser, dummyImage) } returns 3

        val result: UserSettingsResponseDto = userService.getUserSettings(principal)

        assertEquals("테스트유저", result.username)
        assertEquals(5, result.maxFeedbackCount)
        assertEquals(3, result.remainingFeedbackCount)
    }
}
