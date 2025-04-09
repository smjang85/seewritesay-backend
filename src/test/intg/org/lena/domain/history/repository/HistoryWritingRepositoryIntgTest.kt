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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
                HistoryWriting.of(user = user, image = image1, sentence = "First", createdBy = user.id.toString()),
                HistoryWriting.of(user = user, image = image2, sentence = "Second", createdBy = user.id.toString())
            )
        )
    }

    @Test
    @DisplayName("findAllByUserId_유저의_전체_히스토리_조회")
    fun findAllByUserId_유저의_전체_히스토리_조회() {
        val result = historyWritingRepository.findAllByUserId(user.id!!)
        assertEquals(2, result.size)
    }

    @Test
    @DisplayName("findAllByUserIdAndImageId_유저와_이미지로_필터링")
    fun findAllByUserIdAndImageId_유저와_이미지로_필터링() {
        val result = historyWritingRepository.findAllByUserIdAndImageId(user.id!!, image1.id!!)
        assertEquals(1, result.size)
        assertEquals("First", result[0].sentence)
    }

    @Test
    @DisplayName("조건에_맞는_히스토리가_없을_경우_빈_리스트_반환")
    fun 조건에_맞는_히스토리가_없을_경우_빈_리스트_반환() {
        val otherUser = userRepository.save(User.of(email = "other@lena.org", name = "OtherUser"))
        val result = historyWritingRepository.findAllByUserId(otherUser.id!!)
        assertTrue(result.isEmpty())
    }
}
