package org.lena.domain.image.service

import org.lena.api.dto.image.ImageResponseDto
import org.lena.domain.image.entity.Image

interface ImageService {
    fun getDescriptionByImageName(imageName: String): String
    fun findAll(): List<ImageResponseDto>
    fun findById(id: Long): Image
}
