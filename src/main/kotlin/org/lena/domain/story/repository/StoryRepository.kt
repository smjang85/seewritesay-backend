package org.lena.domain.story.repository

import org.lena.domain.story.entity.Story
import org.lena.domain.story.enums.StoryType
import org.lena.domain.story.projection.StoryListProjection
import org.lena.domain.story.projection.StoryTranslationProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StoryRepository : JpaRepository<Story, Long> {

    // 📘 스토리 리스트 (목록용)
    @Query(
        """
        SELECT 
            s.id AS id, 
            s.image_path AS imagePath, 
            s.type AS type, 
            t.title AS title, 
            s.created_at AS createdAt, 
            s.created_by AS createdBy
        FROM pic.stories s
        JOIN (
            SELECT story_id, title, MAX(created_at) AS latest_created_at
            FROM pic.story_translations
            WHERE language_code = :language
            GROUP BY story_id, title
        ) t ON s.id = t.story_id
        """,
        nativeQuery = true
    )
    fun findStoryListProjectionNative(
        @Param("language") language: String
    ): List<StoryListProjection>

    // 📖 단일 스토리 상세 조회 (chapterId 있을 수도 있고, 없을 수도 있음)
    @Query(
        """
        SELECT 
            t.id AS id,
            t.story_id AS storyId,
            t.language_code AS languageCode,
            t.title AS title,
            t.content AS content,
            t.created_at AS createdAt,
            t.created_by AS createdBy
        FROM pic.story_translations t
        JOIN pic.stories s ON s.id = t.story_id
        WHERE s.id = :storyId
          AND (:chapterId IS NULL OR t.chapter_id = :chapterId) -- ✅ chapter_id 비교로 수정!!
          AND t.language_code = :language
        LIMIT 1
        """,
        nativeQuery = true
    )
    fun findTranslation(
        @Param("storyId") storyId: Long,
        @Param("language") language: String,
        @Param("chapterId") chapterId: Long? = null
    ): Optional<StoryTranslationProjection>

    // 📚 타입별 스토리 리스트
    fun findByType(type: StoryType): List<Story>
}
