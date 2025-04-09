package org.lena.api.dto.image

import org.lena.domain.image.entity.Category

data class CategoryResponseDto(
    val id: Long? = null,
    val name: String
) {
    companion object {
        fun from(entity: Category): CategoryResponseDto =
            CategoryResponseDto(
                id = entity.id,
                name = entity.name
            )

        fun fromList(entities: List<Category>): List<CategoryResponseDto> =
            entities.map { from(it) }
    }
}
