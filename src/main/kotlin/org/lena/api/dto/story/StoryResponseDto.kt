package org.lena.api.dto.story

import org.lena.domain.story.entity.Story
import org.lena.domain.story.enums.LanguageCode
import kotlin.collections.find

data class StoryResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val imagePath: String?,
    val languageCode: String
) {
    companion object {
        fun fromEntity(story: Story, language: LanguageCode): StoryResponseDto {
            val translation = story.translations.find { it.languageCode == language.name }
                ?: throw IllegalArgumentException("해당 언어(${language.name})의 번역이 없습니다")

            return StoryResponseDto(
                id = story.id,
                title = translation.title,
                content = translation.content,
                imagePath = story.imagePath,
                languageCode = translation.languageCode
            )
        }
    }
}
