package org.lena.api.dto.story

import java.time.LocalDateTime

data class StoryListResponseDto(
    val id: Long,
    val title: String,
    val imagePath: String?,
    val createdAt: LocalDateTime,
    val createdBy: String?
)