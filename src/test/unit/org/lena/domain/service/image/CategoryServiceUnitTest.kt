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
    @DisplayName("findAll_카테고리_전체조회_성공")
    fun findAll_카테고리_전체조회_성공() {
        every { categoryRepository.findAll() } returns listOf(category)

        val result = categoryService.findAll()

        assertEquals(1, result.size)
        assertEquals("여행", result[0].name)
    }

    @Test
    @DisplayName("findByName_이름기반_조회_성공")
    fun findByName_이름기반_조회_성공() {
        every { categoryRepository.findByName("여행") } returns category

        val result = categoryService.findByName("여행")

        assertNotNull(result)
        assertEquals("여행", result.name)
    }

    @Test
    @DisplayName("save_카테고리명으로_저장_성공")
    fun save_카테고리명으로_저장_성공() {
        val inputName = "여행"
        val categoryToSave = Category.of(inputName)

        every { categoryRepository.save(any()) } returns categoryToSave

        val result = categoryService.save(inputName)

        assertEquals(inputName, result.name)
    }

    @Test
    @DisplayName("findById_아이디기반_조회_성공")
    fun findById_아이디기반_조회_성공() {
        every { categoryRepository.findById(1L) } returns Optional.of(category)

        val result = categoryService.findById(1L)

        assertEquals("여행", result?.name)
    }

    @Test
    @DisplayName("findById_존재하지않으면_예외")
    fun findById_존재하지않으면_예외() {
        every { categoryRepository.findById(99L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalArgumentException> {
            categoryService.findById(99L)
        }

        assertTrue(exception.message!!.contains("해당 ID의 카테고리를 찾을 수 없습니다"))
    }

    @Test
    @DisplayName("findNameById_이름_문자열만_조회_성공")
    fun findNameById_이름_문자열만_조회_성공() {
        every { categoryRepository.findNameById(1L) } returns "여행"

        val result = categoryService.findNameById(1L)

        assertEquals("여행", result)
    }

    @Test
    @DisplayName("findNameById_존재하지않으면_null")
    fun findNameById_존재하지않으면_null() {
        every { categoryRepository.findNameById(999L) } returns null

        val result = categoryService.findNameById(999L)

        assertNull(result)
    }
}
