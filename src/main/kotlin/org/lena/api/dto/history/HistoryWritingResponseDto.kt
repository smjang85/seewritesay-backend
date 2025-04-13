package org.lena.api.dto.history
import java.time.LocalDateTime

data class HistoryWritingResponseDto(
    val id: Long,
    val imageId: Long,
    val imagePath: String,
    val imageName: String,
    val imageDescription: String? = null,
    val sentence: String,
    val grade: String,
    val categoryId: Long,
    val categoryName: String,
)