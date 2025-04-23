package org.lena.domain.story.service

import org.lena.api.dto.story.StoryListResponseDto
import org.lena.api.dto.story.StoryResponseDto
import org.lena.domain.story.entity.Story
import org.lena.domain.story.enums.LanguageCode

interface StoryService {
    fun getAllStories(language: LanguageCode): List<StoryListResponseDto>
    fun getStoryById(id: Long, language: LanguageCode): StoryResponseDto
    fun saveMixedLanguageStory(id: Long, code: String): StoryResponseDto
    fun saveStory(story: Story): Story
}
