package org.lena.domain.feedback.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.domain.feedback.entity.UserFeedback
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
class UserFeedbackRepositoryIntgTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var imageRepository: ImageRepository

    @Autowired
    lateinit var userFeedbackRepository: UserFeedbackRepository

    private lateinit var user: User
    private lateinit var image: Image

    @BeforeEach
    fun setUp() {
        userFeedbackRepository.deleteAll()
        userRepository.deleteAll()
        imageRepository.deleteAll()

        user = userRepository.save(User(email = "test@lena.org", name = "Test User"))
        image = imageRepository.save(
            Image(name = "테스트 이미지", path = "/images/test.jpg", description = "설명", categoryId = 1)
        )
    }

    @Test
    fun `사용자와 이미지로 피드백 조회`() {
        val feedback = UserFeedback(
            user = user,
            image = image,
            remainingCount = 3,
            createdBy = "test"
        )
        userFeedbackRepository.save(feedback)

        val found = userFeedbackRepository.findByUserAndImage(user, image)

        assertNotNull(found)
        assertEquals(3, found?.remainingCount)
    }

    @Test
    fun `피드백 존재하지 않으면 null 반환`() {
        val result = userFeedbackRepository.findByUserAndImage(user, image)
        assertNull(result)
    }
}
