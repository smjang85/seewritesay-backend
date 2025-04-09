package org.lena.domain.service.image

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.image.entity.Category
import org.lena.domain.image.repository.CategoryRepository
import org.lena.infra.image.CategoryServiceImpl
import java.util.*
import kotlin.test.*

class CategoryServiceUnitTest {

    private lateinit var categoryService: CategoryServiceImpl
    private val categoryRepository: CategoryRepository = mockk()

    private val category = Category.of(name = "여행")

    @BeforeEach
    fun setUp() {
        categoryService = CategoryServiceImpl(categoryRepository)
    }

    @Test
    @DisplayName("findAll - 전체 카테고리 조회 성공")
    fun findAll_성공() {
        every { categoryRepository.findAll() } returns listOf(category)

        val result = categoryService.findAll()

        assertEquals(1, result.size)
        assertEquals("여행", result[0].name)
    }

    @Test
    @DisplayName("findByName - 이름으로 카테고리 조회 성공")
    fun findByName_성공() {
        every { categoryRepository.findByName("여행") } returns category

        val result = categoryService.findByName("여행")

        assertNotNull(result)
        assertEquals("여행", result.name)
    }

    @Test
    @DisplayName("save - 카테고리 저장 성공")
    fun save_성공() {
        every { categoryRepository.save(category) } returns category

        val result = categoryService.save(category)

        assertEquals(category, result)
    }

    @Test
    @DisplayName("findById - ID로 카테고리 조회 성공")
    fun findById_성공() {
        every { categoryRepository.findById(1L) } returns Optional.of(category)

        val result = categoryService.findById(1L)

        assertEquals("여행", result?.name)
    }

    @Test
    @DisplayName("findById - 존재하지 않으면 예외 발생")
    fun findById_예외() {
        every { categoryRepository.findById(99L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalArgumentException> {
            categoryService.findById(99L)
        }

        assertTrue(exception.message!!.contains("해당 ID의 카테고리를 찾을 수 없습니다"))
    }

    @Test
    @DisplayName("findNameById - 이름 조회 성공")
    fun findNameById_성공() {
        every { categoryRepository.findNameById(1L) } returns "여행"

        val result = categoryService.findNameById(1L)

        assertEquals("여행", result)
    }
}
