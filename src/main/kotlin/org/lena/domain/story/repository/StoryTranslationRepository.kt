package org.lena.domain.story.repository


import org.lena.domain.story.entity.StoryTranslation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StoryTranslationRepository : JpaRepository<StoryTranslation, Long> {

    fun findByStoryIdAndLanguageCode(storyId: Long, languageCode: String): StoryTranslation?

    @Query("SELECT st.languageCode FROM StoryTranslation st WHERE st.story.id = :storyId")
    fun findCodesByStoryId(@Param("storyId") storyId: Long): List<String>

}
