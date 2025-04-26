package org.lena.domain.story.projection

import java.time.LocalDateTime

interface StoryListProjection {
    fun getId(): Long
    fun getImagePath(): String?
    fun getType(): String?
    fun getTitle(): String
    fun getCreatedAt(): LocalDateTime
    fun getCreatedBy(): String?
}