package org.lena.infra.image

import org.lena.domain.image.entity.Category
import org.lena.domain.image.repository.CategoryRepository
import org.lena.domain.image.service.CategoryService
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    override fun findAll(): List<Category> = categoryRepository.findAll()

    override fun findByName(name: String): Category? = categoryRepository.findByName(name)

    override fun save(category: Category): Category = categoryRepository.save(category)

    override fun findById(categoryId: Long): Category? {
        return categoryRepository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("해당 ID의 카테고리를 찾을 수 없습니다: $categoryId") }
    }

    override fun findNameById(id: Long): String? {
        return categoryRepository.findNameById(id)
    }
}