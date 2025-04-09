package org.lena.domain.image.repository

import org.lena.domain.image.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Category?

    @Query("SELECT c.name FROM Category c WHERE c.id = :categoryId")
    fun findNameById(@Param("categoryId") categoryId: Long): String?
}