package org.lena.domain.image.repository

import org.lena.domain.image.entity.Image
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository : JpaRepository<Image, Long> {
    fun findByName(name: String): Image?
}