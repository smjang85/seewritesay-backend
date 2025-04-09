package org.lena.domain.history.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.domain.history.entity.HistoryWriting
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
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

        user = userRepository.save(User(email = "test@lena.org", name = "TestUser"))
        image1 = imageRepository.save(Image(name = "Image1", path = "/img/1.jpg", description = "desc1", categoryId = 1))
        image2 = imageRepository.save(Image(name = "Image2", path = "/img/2.jpg", description = "desc2", categoryId = 1))

        historyWritingRepository.saveAll(
            listOf(
                HistoryWriting(user = user, image = image1, sentence = "First", createdBy = user.id.toString()),
                HistoryWriting(user = user, image = image2, sentence = "Second", createdBy = user.id.toString())
            )
        )
    }

    @Test
    fun `findAllByUserId - 유저의 전체 히스토리 조회`() {
        val result = historyWritingRepository.findAllByUserId(user.id!!)
        assertEquals(2, result.size)
    }

    @Test
    fun `findAllByUserIdAndImageId - 유저와 이미지로 필터링`() {
        val result = historyWritingRepository.findAllByUserIdAndImageId(user.id!!, image1.id!!)
        assertEquals(1, result.size)
        assertEquals("First", result[0].sentence)
    }

    @Test
    fun `조건에 맞는 히스토리가 없을 경우 빈 리스트 반환`() {
        val otherUser = userRepository.save(User(email = "other@lena.org", name = "OtherUser"))
        val result = historyWritingRepository.findAllByUserId(otherUser.id!!)
        assertTrue(result.isEmpty())
    }
}
