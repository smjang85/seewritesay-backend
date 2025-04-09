package org.lena.domain.image.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.image.entity.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoryRepositoryIntgTest {

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    private lateinit var savedCategory: Category

    @BeforeEach
    fun setup() {
        categoryRepository.deleteAll()
        savedCategory = categoryRepository.save(Category.of(name = "TestCategory"))
    }

    @Test
    @DisplayName("findByName_카테고리_이름으로_조회")
    fun findByName_카테고리_이름으로_조회() {
        val found = categoryRepository.findByName("TestCategory")
        assertEquals(savedCategory.id, found?.id)
        assertEquals("TestCategory", found?.name)
    }

    @Test
    @DisplayName("findByName_존재하지_않는_이름")
    fun findByName_존재하지_않는_이름() {
        val result = categoryRepository.findByName("NonExistent")
        assertNull(result)
    }

    @Test
    @DisplayName("findNameById_ID로_이름_조회")
    fun findNameById_ID로_이름_조회() {
        val result = categoryRepository.findNameById(savedCategory.id!!)
        assertEquals("TestCategory", result)
    }

    @Test
    @DisplayName("findNameById_존재하지_않는_ID")
    fun findNameById_존재하지_않는_ID() {
        val result = categoryRepository.findNameById(999L)
        assertNull(result)
    }
}
