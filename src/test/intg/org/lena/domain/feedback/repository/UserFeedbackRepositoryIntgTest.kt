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
import org.junit.jupiter.api.Test
import kotlin.test.*

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
        image = imageRepository.save(Image.of(name = "테스트 이미지", path = "/test.jpg", categoryId = 1))
    }

    @Test
    @DisplayName("findByUserAndImage_정상조회_성공")
    fun findByUserAndImage_정상조회_성공() {
        // given
        val feedback = UserFeedback.of(user, image, remainingCount = 3, createdBy = "test")
        userFeedbackRepository.save(feedback)

        // when
        val found = userFeedbackRepository.findByUserAndImage(user, image)

        // then
        assertAll(
            "정상적으로 피드백을 조회해야 함",
            { assertNotNull(found) },
            { assertEquals(3, found?.remainingCount) }
        )
    }

    @Test
    @DisplayName("findByUserAndImage_존재하지_않으면_null_반환")
    fun findByUserAndImage_존재하지_않으면_null_반환() {
        val result = userFeedbackRepository.findByUserAndImage(user, image)
        assertNull(result)
    }

    @Test
    @DisplayName("save_동일_유저와_이미지로_덮어쓰기_불가_확인")
    fun save_동일_유저와_이미지로_덮어쓰기_불가_확인() {
        // given
        val initial = UserFeedback.of(user, image, remainingCount = 5, createdBy = "init")
        userFeedbackRepository.save(initial)

        // when - ID 없이 저장 → Unique 제약 조건으로는 막지 않음
        val updated = UserFeedback.of(user, image, remainingCount = 2, createdBy = "update")
        userFeedbackRepository.save(updated)

        // then
        val results = userFeedbackRepository.findAll()
        assertEquals(2, results.size, "중복 방지 로직이 없다면 두 개가 저장됨")
    }

    @Test
    @DisplayName("remainingCount_경계값_테스트")
    fun remainingCount_경계값_테스트() {
        val feedback = UserFeedback.of(user, image, remainingCount = 0, createdBy = "tester")
        val saved = userFeedbackRepository.save(feedback)

        assertAll(
            "0도 정상 저장되어야 함",
            { assertEquals(0, saved.remainingCount) },
            { assertNotNull(saved.id) }
        )
    }
}
