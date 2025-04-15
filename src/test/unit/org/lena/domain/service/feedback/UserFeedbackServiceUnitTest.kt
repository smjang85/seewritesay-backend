package org.lena.domain.service.feedback

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.feedback.entity.UserFeedback
import org.lena.domain.feedback.repository.UserFeedbackRepository
import org.lena.domain.image.entity.Image
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.entity.User
import org.lena.domain.user.service.UserService
import org.lena.infra.feedback.UserFeedbackServiceImpl
import kotlin.test.assertEquals

class UserFeedbackServiceUnitTest {

    private lateinit var userFeedbackService: UserFeedbackServiceImpl
    private val userFeedbackRepository: UserFeedbackRepository = mockk(relaxed = true)
    private val userService: UserService = mockk()
    private val imageService: ImageService = mockk()

    private val user = User.of(email = "test@lena.org", name = "TestUser")
    private val image = Image.of(name = "image", path = "/test.jpg", categoryId = 1)

    @BeforeEach
    fun setUp() {
        userFeedbackService = UserFeedbackServiceImpl(
            userFeedbackRepository = userFeedbackRepository,
            userService = userService,
            imageService = imageService
        )
    }

    @Test
    @DisplayName("getRemainingCount_없을경우_기본값_30_2")
    fun getRemainingCount_없을경우_기본값_30_2() {
        every { userService.findById(1L) } returns user
        every { imageService.findById(10L) } returns image
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns null

        val result = userFeedbackService.getRemainingCount(1L, 10L)

        assertEquals(30, result.writingRemainingCount)
        assertEquals(2, result.readingRemainingCount)
    }

    @Test
    @DisplayName("getRemainingCount_존재하면_기존값_반환")
    fun getRemainingCount_존재하면_기존값_반환() {
        val existing = UserFeedback.of(user, image, 12, 1)

        every { userService.findById(1L) } returns user
        every { imageService.findById(10L) } returns image
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns existing

        val result = userFeedbackService.getRemainingCount(1L, 10L)

        assertEquals(12, result.writingRemainingCount)
        assertEquals(1, result.readingRemainingCount)
    }

    @Test
    @DisplayName("decrementWritingFeedbackCount_없으면_생성후_감소")
    fun decrementWritingFeedbackCount_없으면_생성후_감소() {
        every { userService.findById(1L) } returns user
        every { imageService.findById(10L) } returns image
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns null
        every { userFeedbackRepository.save(any()) } returnsArgument 0

        userFeedbackService.decrementWritingFeedbackCount(1L, 10L)

        verify {
            userFeedbackRepository.save(
                match {
                    it.writing_remaining_count == 29 && it.reading_remaining_count == 2
                }
            )
        }
    }

    @Test
    @DisplayName("decrementWritingFeedbackCount_존재하면_감소")
    fun decrementWritingFeedbackCount_존재하면_감소() {
        val existing = UserFeedback.of(user, image, 5, 2)

        every { userService.findById(1L) } returns user
        every { imageService.findById(10L) } returns image
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns existing
        every { userFeedbackRepository.save(any()) } returnsArgument 0

        userFeedbackService.decrementWritingFeedbackCount(1L, 10L)

        assertEquals(4, existing.writing_remaining_count)
        verify { userFeedbackRepository.save(existing) }
    }

    @Test
    @DisplayName("decrementWritingFeedbackCount_0이면_감소안함")
    fun decrementWritingFeedbackCount_0이면_감소안함() {
        val existing = UserFeedback.of(user, image, 0, 2)

        every { userService.findById(1L) } returns user
        every { imageService.findById(10L) } returns image
        every { userFeedbackRepository.findByUserAndImage(user, image) } returns existing

        userFeedbackService.decrementWritingFeedbackCount(1L, 10L)

        assertEquals(0, existing.writing_remaining_count)
        verify(exactly = 0) { userFeedbackRepository.save(any()) }
    }
}
