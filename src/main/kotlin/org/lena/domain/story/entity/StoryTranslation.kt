package org.lena.domain.story.entity

import jakarta.persistence.*
import org.lena.api.dto.story.StoryResponseDto
import java.time.LocalDateTime

@Entity
@Table(name = "story_translations", schema = "pic")
class StoryTranslation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    var story: Story,

    @Column(name = "language_code", nullable = false)
    var languageCode: String = "",

    @Column(name = "title", nullable = false)
    var title: String = "",

    @Lob
    @Column(columnDefinition = "text", name = "content", nullable = false)
    var content: String = "",

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    var createdBy: String? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    var updatedBy: String? = null
) {
    constructor() : this(0L, Story(), "", "", "")
}
fun StoryTranslation.toDto(): StoryResponseDto {
    return StoryResponseDto(
        id = story.id,
        title = title,
        languageCode = languageCode,
        imagePath = story.imagePath,
        content = content.trim()
    )
}