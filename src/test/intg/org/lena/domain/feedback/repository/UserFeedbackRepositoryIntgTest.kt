package org.lena.domain.feedback.repository

import org.junit.jupiter.api.*
import org.lena.domain.feedback.entity.UserFeedback
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.*
import org.junit.jupiter.api.Test

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
    fun setup() {
        userFeedbackRepository.deleteAll()
        userRepository.deleteAll()
        imageRepository.deleteAll()

        user = userRepository.save(User.of(email = "test@lena.org", name = "테스트 유저"))
        image = imageRepository.save(Image.of(name = "샘플 이미지", path = "/test.jpg", categoryId = 1))
    }

    @Test
    @DisplayName("findByUserAndImage_피드백_정상조회")
    fun findByUserAndImage_피드백_정상조회() {
        // given
        val feedback = UserFeedback.of(
            user = user,
            image = image,
            writing_remaining_count = 3,
            reading_remaining_count = 2,
            createdBy = "test"
        )
        userFeedbackRepository.save(feedback)

        // when
        val found = userFeedbackRepository.findByUserAndImage(user, image)

        // then
        assertAll(
            { assertNotNull(found) },
            { assertEquals(3, found?.writing_remaining_count) },
            { assertEquals(2, found?.reading_remaining_count) }
        )
    }

    @Test
    @DisplayName("findByUserAndImage_없는경우_null반환")
    fun findByUserAndImage_없는경우_null반환() {
        // when
        val result = userFeedbackRepository.findByUserAndImage(user, image)

        // then
        assertNull(result)
    }

    @Test
    @DisplayName("save_동일유저이미지_중복저장")
    fun save_동일유저이미지_중복저장() {
        // given
        val feedback1 = UserFeedback.of(user, image, 3, 1, "first")
        val feedback2 = UserFeedback.of(user, image, 2, 0, "second")
        userFeedbackRepository.save(feedback1)

        // when - unique 제약조건이 없으면 덮어쓰기 되지 않음
        assertFailsWith<Exception> {
            userFeedbackRepository.save(feedback2)
        }

        // then
        val all = userFeedbackRepository.findAll()
        assertEquals(1, all.size)
    }

    @Test
    @DisplayName("save_remainingCount_경계값_0")
    fun save_remainingCount_경계값_0() {
        // given
        val feedback = UserFeedback.of(user, image, 0, 0, "test")

        // when
        val saved = userFeedbackRepository.save(feedback)

        // then
        assertAll(
            { assertEquals(0, saved.writing_remaining_count) },
            { assertEquals(0, saved.reading_remaining_count) },
            { assertNotNull(saved.id) }
        )
    }
}
