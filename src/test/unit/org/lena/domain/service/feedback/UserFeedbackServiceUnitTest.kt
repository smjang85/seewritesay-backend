package org.lena.domain.service.feedback

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.feedback.entity.UserFeedback
import org.lena.domain.feedback.repository.UserFeedbackRepository
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import org.lena.infra.feedback.UserFeedbackServiceImpl
import kotlin.test.assertEquals

class UserFeedbackServiceUnitTest {

    private lateinit var userFeedbackService: UserFeedbackServiceImpl
    private val userFeedbackRepository: UserFeedbackRepository = mockk(relaxed = true)

    private val user = User.of(email = "test@lena.org", name = "TestUser")
    private val image = Image.of(name = "test", path = "/img", categoryId = 1)

    @BeforeEach
    fun setUp() {
        userFeedbackService = UserFeedbackServiceImpl(userFeedbackRepository)
        clearMocks(userFeedbackRepository)
    }

    @Test
    @DisplayName("getRemainingCount - 존재하지 않으면 기본값 5 반환")
    fun getRemainingCount_없으면_기본값5() {
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns null

        val count = userFeedbackService.getRemainingCount(user, image)

        assertEquals(5, count)
    }

    @Test
    @DisplayName("getRemainingCount - 존재하면 해당 값 반환")
    fun getRemainingCount_존재하면_해당값() {
        val feedback = UserFeedback.of(user, image, remainingCount = 3)
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns feedback

        val count = userFeedbackService.getRemainingCount(user, image)

        assertEquals(3, count)
    }

    @Test
    @DisplayName("decrementFeedbackCount - 새로 만들고 감소")
    fun decrementFeedbackCount_생성후감소() {
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns null
        every { userFeedbackRepository.save(any()) } returnsArgument 0

        userFeedbackService.decrementFeedbackCount(user, image)

        verify { userFeedbackRepository.save(match { it.remainingCount == 4 }) }
    }

    @Test
    @DisplayName("decrementFeedbackCount - 기존 값에서 감소")
    fun decrementFeedbackCount_기존값감소() {
        val existing = UserFeedback.of(user, image, remainingCount = 2)
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns existing
        every { userFeedbackRepository.save(any()) } returnsArgument 0

        userFeedbackService.decrementFeedbackCount(user, image)

        assertEquals(1, existing.remainingCount)
        verify { userFeedbackRepository.save(existing) }
    }

    @Test
    @DisplayName("resetFeedbackCount - 없으면 생성")
    fun resetFeedbackCount_없으면_생성() {
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns null
        every { userFeedbackRepository.save(any()) } returnsArgument 0

        userFeedbackService.resetFeedbackCount(user, image, 10)

        verify { userFeedbackRepository.save(match { it.remainingCount == 10 }) }
    }

    @Test
    @DisplayName("resetFeedbackCount - 기존 값 덮어쓰기")
    fun resetFeedbackCount_기존값_덮어쓰기() {
        val existing = UserFeedback.of(user, image, remainingCount = 2)
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns existing
        every { userFeedbackRepository.save(any()) } returnsArgument 0

        userFeedbackService.resetFeedbackCount(user, image, 7)

        assertEquals(7, existing.remainingCount)
        verify { userFeedbackRepository.save(existing) }
    }
}
