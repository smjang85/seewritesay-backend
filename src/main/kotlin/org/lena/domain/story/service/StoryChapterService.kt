package org.lena.domain.story.service

import org.lena.api.dto.story.ChapterDto

interface StoryChapterService {
    fun getChapters(storyId: Long, lang: String): List<ChapterDto>
}