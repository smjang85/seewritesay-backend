package org.lena.domain.image.service

import org.lena.domain.image.entity.Category

interface CategoryService {
    fun findAll(): List<Category>
    fun findByName(name: String): Category?
    fun findNameById(id: Long): String?
    fun save(category: Category): Category
    fun findById(id: Long): Category?
}