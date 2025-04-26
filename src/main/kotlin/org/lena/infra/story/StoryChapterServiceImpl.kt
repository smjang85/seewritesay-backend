package org.lena.infra.story

import mu.KLogging
import org.lena.api.dto.story.ChapterDto
import org.lena.domain.story.repository.StoryChapterRepository
import org.lena.domain.story.service.StoryChapterService
import org.springframework.stereotype.Service

@Service
class StoryChapterServiceImpl(
    private val storyChapterRepository: StoryChapterRepository,
) : StoryChapterService {

    companion object : KLogging()

    override fun getChapters(storyId: Long, lang: String): List<ChapterDto> {
        val chapters = storyChapterRepository.findChaptersWithTranslation(storyId, lang)
        logger.debug("getChapters service chapters={}", chapters);

        return chapters
    }

}
