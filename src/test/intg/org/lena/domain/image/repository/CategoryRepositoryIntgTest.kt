package org.lena.domain.image.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.domain.image.entity.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
class CategoryRepositoryIntgTest {

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    private lateinit var savedCategory: Category

    @BeforeEach
    fun setup() {
        categoryRepository.deleteAll()

        savedCategory = categoryRepository.save(Category(name = "TestCategory"))
    }

    @Test
    fun `findByName - 카테고리 이름으로 조회`() {
        val found = categoryRepository.findByName("TestCategory")
        assertEquals(savedCategory.id, found?.id)
        assertEquals("TestCategory", found?.name)
    }

    @Test
    fun `findByName - 존재하지 않는 이름`() {
        val result = categoryRepository.findByName("NonExistent")
        assertNull(result)
    }

    @Test
    fun `findNameById - ID로 이름 조회`() {
        val result = categoryRepository.findNameById(savedCategory.id!!)
        assertEquals("TestCategory", result)
    }

    @Test
    fun `findNameById - 존재하지 않는 ID`() {
        val result = categoryRepository.findNameById(999L)
        assertNull(result)
    }
}
