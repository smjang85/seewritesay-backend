package org.lena.api.dto.history

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class HistoryWritingRequestDto(

    @field:NotNull(message = "이미지 ID는 필수입니다.")
    val imageId: Long,

    @field:NotBlank(message = "문장은 비어 있을 수 없습니다.")
    val sentence: String,

    @field:NotBlank(message = "등급은 비어 있을 수 없습니다.")
    val grade: String,

)
