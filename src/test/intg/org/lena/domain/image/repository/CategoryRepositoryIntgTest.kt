package org.lena.domain.image.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.domain.image.entity.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.*

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
        savedCategory = categoryRepository.save(Category.of(name = "테스트카테고리", createdBy = "test"))
    }

    @Test
    @DisplayName("findByName_카테고리이름으로조회_성공")
    fun findByName_카테고리이름으로조회_성공() {
        val found = categoryRepository.findByName("테스트카테고리")
        assertNotNull(found)
        assertEquals(savedCategory.id, found.id)
        assertEquals("테스트카테고리", found.name)
    }

    @Test
    @DisplayName("findByName_존재하지않는이름_조회결과_null")
    fun findByName_존재하지않는이름_조회결과_null() {
        val result = categoryRepository.findByName("없는카테고리")
        assertNull(result)
    }

    @Test
    @DisplayName("findNameById_ID로카테고리이름조회_성공")
    fun findNameById_ID로카테고리이름조회_성공() {
        val name = categoryRepository.findNameById(savedCategory.id!!)
        assertEquals("테스트카테고리", name)
    }

    @Test
    @DisplayName("findNameById_존재하지않는ID_null반환")
    fun findNameById_존재하지않는ID_null반환() {
        val result = categoryRepository.findNameById(99999L)
        assertNull(result)
    }
}
