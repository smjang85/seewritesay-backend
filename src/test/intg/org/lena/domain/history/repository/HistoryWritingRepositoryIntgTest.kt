package org.lena.domain.history.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.history.entity.HistoryWriting
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HistoryWritingRepositoryIntgTest {

    @Autowired
    lateinit var historyWritingRepository: HistoryWritingRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var imageRepository: ImageRepository

    private lateinit var user: User
    private lateinit var image1: Image
    private lateinit var image2: Image

    @BeforeEach
    fun setup() {
        historyWritingRepository.deleteAll()
        userRepository.deleteAll()
        imageRepository.deleteAll()

        user = userRepository.save(User.of(email = "test@lena.org", name = "TestUser"))
        image1 = imageRepository.save(Image.of(name = "Image1", path = "/img/1.jpg", categoryId = 1, description = "desc1"))
        image2 = imageRepository.save(Image.of(name = "Image2", path = "/img/2.jpg", categoryId = 1, description = "desc2"))

        historyWritingRepository.saveAll(
            listOf(
                HistoryWriting.of(user, image1, sentence = "First sentence", grade = "A", createdBy = user.id.toString()),
                HistoryWriting.of(user, image2, sentence = "Second sentence", grade = "B", createdBy = user.id.toString())
            )
        )
    }

    @Test
    @DisplayName("findAllByUserId_사용자전체히스토리_조회")
    fun findAllByUserId_사용자전체히스토리_조회() {
        val results = historyWritingRepository.findAllByUserId(user.id!!)
        assertEquals(2, results.size)
    }

    @Test
    @DisplayName("findAllByUserIdAndImageId_사용자_이미지별_히스토리_조회")
    fun findAllByUserIdAndImageId_사용자_이미지별_히스토리_조회() {
        val results = historyWritingRepository.findAllByUserIdAndImageId(user.id!!, image1.id!!)
        assertEquals(1, results.size)
        assertEquals("First sentence", results.first().sentence)
    }

    @Test
    @DisplayName("findByUserIdAndImageId_단건_조회_성공")
    fun findByUserIdAndImageId_단건_조회_성공() {
        val result = historyWritingRepository.findByUserIdAndImageId(user.id!!, image2.id!!)
        assertNotNull(result)
        assertEquals("Second sentence", result.sentence)
    }

    @Test
    @DisplayName("findByIdAndUser_아이디와사용자로_단건_조회")
    fun findByIdAndUser_아이디와사용자로_단건_조회() {
        val saved = historyWritingRepository.findAllByUserId(user.id!!).first()
        val result = historyWritingRepository.findByIdAndUser(saved.id!!, user)
        assertNotNull(result)
        assertEquals(saved.id, result.id)
    }

    @Test
    @DisplayName("findAllByUserId_다른사용자_히스토리없음")
    fun findAllByUserId_다른사용자_히스토리없음() {
        val otherUser = userRepository.save(User.of(email = "other@lena.org", name = "OtherUser"))
        val result = historyWritingRepository.findAllByUserId(otherUser.id!!)
        assertTrue(result.isEmpty())
    }
}
