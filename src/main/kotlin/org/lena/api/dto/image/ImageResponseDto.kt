package org.lena.api.dto.image

import org.lena.domain.image.entity.Image

data class ImageResponseDto(
    val id: Long,
    val name: String,
    val path: String,
    val categoryName: String?,
    val description: String?
)
