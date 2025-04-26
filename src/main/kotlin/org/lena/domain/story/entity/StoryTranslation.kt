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
    var story: Story = Story(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    var chapter: StoryChapter? = null,

    @Column(name = "language_code", nullable = false)
    var languageCode: String = "",

    @Column(name = "title", nullable = false)
    var title: String = "",

    @Column(name = "source")
    var source: String? = null,

    @Lob
    @Column(name = "content", columnDefinition = "text", nullable = false)
    var content: String = "",

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    var createdBy: String? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    var updatedBy: String? = null
) {
    constructor() : this(
        id = 0L,
        story = Story(),
        languageCode = "",
        title = "",
        source = null,
        content = "",
        createdAt = LocalDateTime.now()
    )
}

fun StoryTranslation.toDto(): StoryResponseDto {
    return StoryResponseDto(
        id = story.id,
        type = story.type.toString(),
        title = title,
        languageCode = languageCode,
        source = source,
        imagePath = story.imagePath,
        content = content.trim()
        // 필요 시 chapterId 포함도 가능
    )
}