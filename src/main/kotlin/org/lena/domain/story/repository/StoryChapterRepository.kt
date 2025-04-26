package org.lena.domain.story.repository

import org.lena.api.dto.story.ChapterDto
import org.lena.domain.story.entity.StoryChapter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StoryChapterRepository : JpaRepository<StoryChapter, Long> {
    @Query("""
    SELECT new org.lena.api.dto.story.ChapterDto(
        c.id,
        s.id,
        t.title,
        c.chapterOrder,
        c.isActive
    )
    FROM StoryChapter c
    JOIN c.story s
    JOIN StoryTranslation t ON t.story.id = s.id
    WHERE s.id = :storyId
      AND t.languageCode = :lang
      AND c.isActive = true
    ORDER BY c.chapterOrder ASC
""")
    fun findChaptersWithTranslation(@Param("storyId") storyId: Long, @Param("lang") lang: String): List<ChapterDto>

}