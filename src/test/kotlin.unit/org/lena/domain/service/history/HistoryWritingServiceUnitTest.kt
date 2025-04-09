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
import org.lena.domain.user.entity.User
import org.lena.infra.history.HistoryWritingServiceImpl
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HistoryWritingServiceUnitTest {

    private lateinit var historyWritingService: HistoryWritingServiceImpl
    private val historyWritingRepository: HistoryWritingRepository = mockk()
    private val categoryService: CategoryService = mockk()

    private val user = User.of(email = "test@lena.org", name = "테스트유저")
    private val image = Image.of(name = "이미지", path = "/img.jpg", categoryId = 1, description = "설명")

    @BeforeEach
    fun setUp() {
        historyWritingService = HistoryWritingServiceImpl(historyWritingRepository, categoryService)
        clearMocks(historyWritingRepository, categoryService)
    }

    @Test
    @DisplayName("getHistory - 이미지 ID 없이 전체 조회")
    fun getHistory_전체조회() {
        val history = HistoryWriting.of(user, image, sentence = "문장1", category = "여행")
        every { historyWritingRepository.findAllByUserId(user.id!!) } returns listOf(history)

        val result = historyWritingService.getHistory(user, null)

        assertEquals(1, result.size)
        assertEquals("문장1", result[0].sentence)
    }

    @Test
    @DisplayName("getHistory - 특정 이미지 ID 조회")
    fun getHistory_이미지ID조회() {
        val history = HistoryWriting.of(user, image, sentence = "문장2", category = "가족")
        every { historyWritingRepository.findAllByUserIdAndImageId(user.id!!, image.id!!) } returns listOf(history)

        val result = historyWritingService.getHistory(user, image.id)

        assertEquals(1, result.size)
        assertEquals("문장2", result[0].sentence)
    }

    @Test
    @DisplayName("saveHistory - 저장 성공")
    fun saveHistory_저장성공() {
        every { categoryService.findById(image.categoryId) } returns Category.of(name = "학교")
        every { historyWritingRepository.save(any()) } answers { firstArg() }

        val result = historyWritingService.saveHistory(user, image, "테스트 문장")

        assertEquals("테스트 문장", result.sentence)
        assertEquals(image.id, result.imageId)
        assertEquals("학교", result.categoryName)
    }

    @Test
    @DisplayName("getUserHistoryWithCategory - 카테고리 포함 전체 조회")
    fun getUserHistoryWithCategory_전체조회() {
        val history = HistoryWriting.of(user, image, sentence = "문장3", category = "가족")
        every { historyWritingRepository.findAllByUserId(user.id!!) } returns listOf(history)
        every { categoryService.findNameById(image.categoryId) } returns "가족"

        val result = historyWritingService.getUserHistoryWithCategory(user)

        assertEquals(1, result.size)
        assertEquals("가족", result[0].categoryName)
    }
}
