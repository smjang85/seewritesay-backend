package org.lena.domain.image.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.image.entity.Image
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFalse

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ImageRepositoryIntgTest {

    @Autowired
    lateinit var imageRepository: ImageRepository

    private lateinit var savedImage: Image

    @BeforeEach
    fun setup() {
        imageRepository.deleteAll()

        savedImage = imageRepository.save(
            Image.of(
                name = "샘플 이미지",
                path = "/images/sample.jpg",
                categoryId = 1L,
                description = "샘플 이미지입니다"
            )
        )
    }

    @Test
    @DisplayName("findById_이미지_조회_성공")
    fun findById_이미지_조회_성공() {
        val found = imageRepository.findById(savedImage.id!!)
        assertNotNull(found.orElse(null))
        assertEquals("샘플 이미지", found.get().name)
    }

    @Test
    @DisplayName("findById_존재하지_않는_이미지")
    fun findById_존재하지_않는_이미지() {
        val result = imageRepository.findById(999L)
        assertFalse(result.isPresent)
    }

    @Test
    @DisplayName("deleteById_이미지_삭제_성공")
    fun deleteById_이미지_삭제_성공() {
        imageRepository.deleteById(savedImage.id!!)
        val deleted = imageRepository.findById(savedImage.id!!)
        assertFalse(deleted.isPresent)
    }
}
