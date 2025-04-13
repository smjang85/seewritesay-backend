package org.lena.domain.image.service

import org.lena.api.dto.image.ImageResponseDto
import org.lena.domain.image.entity.Image

interface ImageService {
    fun getDescriptionByImageId(imageId: Long): String
    fun findAll(): List<ImageResponseDto>
    fun findById(imageId: Long): Image
}
