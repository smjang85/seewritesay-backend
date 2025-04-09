package org.lena.domain.service.image

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.api.dto.image.ImageResponseDto
import org.lena.domain.image.entity.Category
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.CategoryRepository
import org.lena.domain.image.repository.ImageRepository
import org.lena.infra.image.ImageServiceImpl
import java.util.*
import kotlin.test.*

class ImageServiceUnitTest {

    private lateinit var imageService: ImageServiceImpl
    private val imageRepository: ImageRepository = mockk()
    private val categoryRepository: CategoryRepository = mockk()

    private val image = Image.of(
        name = "Sample",
        path = "/sample.jpg",
        categoryId = 1L,
        description = "설명"
    )

    private val category = Category.of(name = "여행")

    @BeforeEach
    fun setUp() {
        imageService = ImageServiceImpl(imageRepository, categoryRepository)
    }

    @Test
    @DisplayName("getDescriptionByImageId - 설명 정상 반환")
    fun getDescriptionByImageId_성공() {
        every { imageRepository.findById(1L) } returns Optional.of(image)

        val result = imageService.getDescriptionByImageId(1L)

        assertEquals("설명", result)
    }

    @Test
    @DisplayName("getDescriptionByImageId - 이미지 없음 예외")
    fun getDescriptionByImageId_이미지없음() {
        every { imageRepository.findById(99L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalStateException> {
            imageService.getDescriptionByImageId(99L)
        }

        assertTrue(exception.message!!.contains("존재하지 않는 이미지 ID"))
    }

    @Test
    @DisplayName("getDescriptionByImageId - 설명 null 예외")
    fun getDescriptionByImageId_설명없음() {
        val imageWithoutDesc = Image.of(
            name = "NoDesc",
            path = "/no.jpg",
            categoryId = 1L,
            description = null
        )
        every { imageRepository.findById(1L) } returns Optional.of(imageWithoutDesc)

        val exception = assertFailsWith<IllegalStateException> {
            imageService.getDescriptionByImageId(1L)
        }

        assertTrue(exception.message!!.contains("이미지 설명이 비어 있습니다"))
    }

    @Test
    @DisplayName("findAll - 전체 이미지 + 카테고리명 매핑")
    fun findAll_성공() {
        val categoryMap = mapOf(1L to "여행")
        every { imageRepository.findAll() } returns listOf(image)
        every { categoryRepository.findAll() } returns listOf(
            Category.of(name = "여행")
        )

        val result: List<ImageResponseDto> = imageService.findAll()

        assertEquals(1, result.size)
        assertEquals("여행", result[0].categoryName)
        assertEquals("설명", result[0].description)
    }

    @Test
    @DisplayName("findById - 정상 조회")
    fun findById_정상조회() {
        every { imageRepository.findById(1L) } returns Optional.of(image)

        val result = imageService.findById(1L)

        assertEquals("Sample", result.name)
    }

    @Test
    @DisplayName("findById - 이미지 없음 예외 발생")
    fun findById_예외() {
        every { imageRepository.findById(999L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalArgumentException> {
            imageService.findById(999L)
        }

        assertTrue(exception.message!!.contains("이미지 ID에 해당하는 정보가 없습니다"))
    }
}
