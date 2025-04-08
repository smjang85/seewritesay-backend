package org.lena.api.dto.writinghistory
import java.time.LocalDateTime

data class WritingHistoryResponse(
    val id: Long,
    val imageId: Long,
    val imagePath: String,
    val imageName: String,
    val imageDescription: String? = null,
    val sentence: String,
    val createdAt: LocalDateTime
)