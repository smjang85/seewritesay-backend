package org.lena.api.dto.story

import org.lena.domain.story.entity.StoryChapter


data class ChapterDto(
    val id: Long,
    val storyId: Long,
    val title: String,
    val order: Int,
    val isActive: Boolean
) {
    companion object {
        fun from(entity: StoryChapter): ChapterDto {
            return ChapterDto(
                id = entity.id,
                storyId = entity.story.id,
                title = "", // 다국어로 조회할 것이므로 일단 빈 값 처리
                order = entity.chapterOrder,
                isActive = entity.isActive
            )
        }
    }
}