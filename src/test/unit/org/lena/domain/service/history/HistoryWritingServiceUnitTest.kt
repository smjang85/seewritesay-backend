package org.lena.domain.service.history

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.api.dto.history.HistoryWritingResponseDto
import org.lena.domain.history.entity.HistoryWriting
import org.lena.domain.history.repository.HistoryWritingRepository
import org.lena.domain.image.entity.Category
import org.lena.domain.image.entity.Image
import org.lena.domain.image.service.CategoryService
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.entity.User
import org.lena.domain.user.service.UserService
import org.lena.infra.history.HistoryWritingServiceImpl
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HistoryWritingServiceUnitTest {

    private lateinit var historyWritingService: HistoryWritingServiceImpl

    private val historyWritingRepository: HistoryWritingRepository = mockk()
    private val userService: UserService = mockk()
    private val imageService: ImageService = mockk()
    private val categoryService: CategoryService = mockk()

    private val user = User.of(email = "test@lena.org", name = "테스트유저")
    private val image = Image.of(name = "이미지", path = "/img.jpg", categoryId = 1L, description = "설명")

    @BeforeEach
    fun setup() {
        historyWritingService = HistoryWritingServiceImpl(
            historyWritingRepository,
            userService,
            imageService,
            categoryService
        )
        clearMocks(historyWritingRepository, userService, imageService, categoryService)
    }

    @Test
    @DisplayName("getHistory - 전체 조회 성공")
    fun getHistory_전체조회() {
        val history = HistoryWriting.of(user, image, sentence = "문장1", grade = "A", category = "여행")
        every { historyWritingRepository.findAllByUserId(1L) } returns listOf(history)
        every { categoryService.findNameById(image.categoryId) } returns "여행"

        val result = historyWritingService.getHistory(userId = 1L, imageId = null)

        assertEquals(1, result.size)
        assertEquals("문장1", result[0].sentence)
        assertEquals("여행", result[0].categoryName)
    }

    @Test
    @DisplayName("getHistory - 특정 이미지 조회 성공")
    fun getHistory_이미지조회() {
        val history = HistoryWriting.of(user, image, sentence = "문장2", grade = "B", category = "가족")
        every { historyWritingRepository.findAllByUserIdAndImageId(1L, 2L) } returns listOf(history)
        every { categoryService.findNameById(image.categoryId) } returns "가족"

        val result = historyWritingService.getHistory(userId = 1L, imageId = 2L)

        assertEquals(1, result.size)
        assertEquals("문장2", result[0].sentence)
        assertEquals("가족", result[0].categoryName)
    }

    @Test
    @DisplayName("saveHistory - 새로 저장")
    fun saveHistory_신규저장() {
        every { userService.findById(1L) } returns user
        every { imageService.findById(2L) } returns image
        every { categoryService.findById(image.categoryId) } returns Category.of(name = "학교")
        every { historyWritingRepository.findByUserIdAndImageId(1L, 2L) } returns null
        every { historyWritingRepository.save(any()) } answers { firstArg() }

        val result = historyWritingService.saveHistory(1L, 2L, "테스트 문장", "A")

        assertEquals("테스트 문장", result.sentence)
        assertEquals("학교", result.categoryName)
        assertEquals(2L, result.imageId)
    }

    @Test
    @DisplayName("getUserHistoryWithCategory - 전체 조회 성공")
    fun getUserHistoryWithCategory_성공() {
        val history = HistoryWriting.of(user, image, sentence = "문장3", grade = "A", category = "자연")
        every { historyWritingRepository.findAllByUserId(1L) } returns listOf(history)
        every { categoryService.findNameById(image.categoryId) } returns "자연"

        val result = historyWritingService.getUserHistoryWithCategory(1L)

        assertEquals(1, result.size)
        assertEquals("자연", result[0].categoryName)
    }

    @Test
    @DisplayName("deleteHistoryById - 정상 삭제")
    fun deleteHistoryById_성공() {
        val history = HistoryWriting.of(user, image, sentence = "문장4", grade = "A", category = "기타")
        every { userService.findById(1L) } returns user
        every { historyWritingRepository.findByIdAndUser(10L, user) } returns history
        every { historyWritingRepository.delete(history) } just Runs

        historyWritingService.deleteHistoryById(1L, 10L)

        verify { historyWritingRepository.delete(history) }
    }
}
