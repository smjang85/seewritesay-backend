package org.lena.api.dto.story

import org.lena.domain.story.entity.Story
import org.lena.domain.story.enums.LanguageCode
import java.time.LocalDateTime

data class StoryListResponseDto(
    val id: Long,
    val imagePath: String?,
    val type: String,
    val title: String,
    val createdAt: LocalDateTime,
    val createdBy: String?
){
    companion object {
        fun fromEntity(entity: Story, language: LanguageCode): StoryListResponseDto {
            val translation = entity.translations.find { it.languageCode == language.name }
                ?: throw IllegalArgumentException("해당 언어(${language.name}) 번역이 없습니다")

            return StoryListResponseDto(
                id = entity.id,
                imagePath = entity.imagePath,
                type = entity.type.code,
                title = translation.title,
                createdAt = entity.createdAt,
                createdBy = entity.createdBy
            )
        }
    }
}