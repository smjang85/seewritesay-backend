package org.lena.domain.story.projection

import java.time.LocalDateTime

interface StoryTranslationProjection {
    fun getId(): Long
    fun getStoryId(): Long
    fun getLanguageCode(): String
    fun getTitle(): String
    fun getContent(): String
    fun getCreatedAt(): LocalDateTime
    fun getCreatedBy(): String?
}