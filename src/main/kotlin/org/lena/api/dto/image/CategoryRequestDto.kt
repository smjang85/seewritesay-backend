package org.lena.api.dto.image

import jakarta.validation.constraints.NotBlank

data class CategoryRequestDto(
    val id: Long? = null,

    @field:NotBlank(message = "카테고리 이름은 비어 있을 수 없습니다.")
    val name: String
)
