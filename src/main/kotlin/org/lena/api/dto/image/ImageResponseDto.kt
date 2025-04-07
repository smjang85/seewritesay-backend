package org.lena.api.dto.image

data class ImageResponseDto(
    val id: Long,
    val name: String,
    val path: String,
    val category: String?,
    val description: String?
)
