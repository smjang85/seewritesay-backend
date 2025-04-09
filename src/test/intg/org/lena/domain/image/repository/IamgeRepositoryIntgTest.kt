package org.lena.domain.image.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.domain.image.entity.Image
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
class ImageRepositoryIntgTest {

    @Autowired
    lateinit var imageRepository: ImageRepository

    private lateinit var savedImage: Image

    @BeforeEach
    fun setup() {
        imageRepository.deleteAll()

        savedImage = imageRepository.save(
            Image(
                name = "샘플 이미지",
                path = "/images/sample.jpg",
                description = "샘플 이미지입니다",
                categoryId = 1L
            )
        )
    }

    @Test
    fun `이미지 저장 및 조회`() {
        val found = imageRepository.findById(savedImage.id!!)
        assertNotNull(found.orElse(null))
        assertEquals("샘플 이미지", found.get().name)
    }

    @Test
    fun `존재하지 않는 이미지 조회`() {
        val result = imageRepository.findById(999L)
        assertEquals(false, result.isPresent)
    }

    @Test
    fun `이미지 삭제`() {
        imageRepository.deleteById(savedImage.id!!)
        val deleted = imageRepository.findById(savedImage.id!!)
        assertEquals(false, deleted.isPresent)
    }
}
