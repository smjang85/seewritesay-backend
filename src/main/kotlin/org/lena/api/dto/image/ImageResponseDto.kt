package org.lena.api.dto.image

import org.lena.domain.image.entity.Image

data class ImageResponseDto(
    val id: Long,
    val name: String,
    val path: String,
    val categoryName: String?,
    val description: String?
) {
    companion object {
        fun from(entity: Image, categoryName: String?): ImageResponseDto =
            ImageResponseDto(
                id = entity.id,
                name = entity.name,
                path = entity.path,
                categoryName = categoryName,
                description = entity.description
            )

        fun fromList(entities: List<Image>, categoryMap: Map<Long, String?>): List<ImageResponseDto> =
            entities.map { from(it, categoryMap[it.categoryId]) }
    }
}
