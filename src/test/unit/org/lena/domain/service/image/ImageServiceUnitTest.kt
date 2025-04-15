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
import org.lena.domain.image.service.ImageServiceImpl
import java.util.*
import kotlin.test.*

class ImageServiceUnitTest {

    private lateinit var imageService: ImageServiceImpl
    private val imageRepository: ImageRepository = mockk()
    private val categoryRepository: CategoryRepository = mockk()

    private lateinit var image: Image
    private lateinit var category: Category

    @BeforeEach
    fun setUp() {
        imageService = ImageServiceImpl(imageRepository, categoryRepository)

        category = Category.of(name = "여행").apply {
            // ID 설정을 위한 리플렉션 또는 JPA 저장 가정
            val field = this::class.java.getDeclaredField("id")
            field.isAccessible = true
            field.set(this, 1L)
        }

        image = Image.of(
            name = "Sample",
            path = "/sample.jpg",
            categoryId = category.id,
            description = "설명"
        )
    }

    @Test
    @DisplayName("getDescriptionByImageId_정상조회")
    fun getDescriptionByImageId_정상조회() {
        every { imageRepository.findById(1L) } returns Optional.of(image)

        val result = imageService.getDescriptionByImageId(1L)

        assertEquals("설명", result)
    }

    @Test
    @DisplayName("getDescriptionByImageId_이미지없음_예외")
    fun getDescriptionByImageId_이미지없음_예외() {
        every { imageRepository.findById(99L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalStateException> {
            imageService.getDescriptionByImageId(99L)
        }

        assertTrue(exception.message!!.contains("존재하지 않는 이미지 ID"))
    }

    @Test
    @DisplayName("getDescriptionByImageId_설명없음_예외")
    fun getDescriptionByImageId_설명없음_예외() {
        val imageWithoutDesc = Image.of(
            name = "NoDesc",
            path = "/no.jpg",
            categoryId = category.id,
            description = null
        )

        every { imageRepository.findById(1L) } returns Optional.of(imageWithoutDesc)

        val exception = assertFailsWith<IllegalStateException> {
            imageService.getDescriptionByImageId(1L)
        }

        assertTrue(exception.message!!.contains("이미지 설명이 비어 있습니다"))
    }

    @Test
    @DisplayName("findAll_카테고리명_매핑_정상")
    fun findAll_카테고리명_매핑_정상() {
        every { imageRepository.findAll() } returns listOf(image)
        every { categoryRepository.findAll() } returns listOf(category)

        val result: List<ImageResponseDto> = imageService.findAll()

        assertEquals(1, result.size)
        assertEquals("여행", result[0].categoryName)
        assertEquals("설명", result[0].description)
    }

    @Test
    @DisplayName("findById_정상조회")
    fun findById_정상조회() {
        every { imageRepository.findById(1L) } returns Optional.of(image)

        val result = imageService.findById(1L)

        assertEquals("Sample", result.name)
    }

    @Test
    @DisplayName("findById_이미지없음_예외")
    fun findById_이미지없음_예외() {
        every { imageRepository.findById(999L) } returns Optional.empty()

        val exception = assertFailsWith<IllegalArgumentException> {
            imageService.findById(999L)
        }

        assertTrue(exception.message!!.contains("이미지 ID에 해당하는 정보가 없습니다"))
    }
}
