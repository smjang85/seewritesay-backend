package org.lena.domain.story.repository

import org.lena.domain.story.entity.Story
import org.lena.domain.story.entity.StoryTranslation
import org.lena.domain.story.enums.LanguageCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : JpaRepository<Story, Long> {

    @Query(
        """
        SELECT s.id, s.image_path, t.title, s.created_at, s.created_by
        FROM pic.stories s
        JOIN pic.story_translations t ON s.id = t.story_id
        WHERE t.language_code = :language
        ORDER BY s.created_at DESC
        """,
        nativeQuery = true
    )
    fun findAllWithTranslation(@Param("language") language: String): List<Array<Any>>

    @Query(
        """
        SELECT t.id, t.story_id, t.language_code, t.title, t.content, t.created_at, t.created_by, t.updated_at, t.updated_by
        FROM pic.story_translations t
        JOIN pic.stories s ON s.id = t.story_id
        WHERE s.id = :id 
        AND t.language_code = :language
        LIMIT 1
        """,
        nativeQuery = true
    )
    fun findByIdWithTranslation(@Param("id") id: Long, @Param("language") language: String): Any?

    @Query(
        """
        SELECT s FROM Story s 
        WHERE s.id NOT IN (
            SELECT st.story.id FROM StoryTranslation st 
            WHERE st.languageCode IN :mixedCodes
        )
        """)
    fun findStoriesWithoutMixedTranslations(@Param("mixedCodes") mixedCodes: List<String>): List<Story>
}



