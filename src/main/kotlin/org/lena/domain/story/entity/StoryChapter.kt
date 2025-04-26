package org.lena.domain.story.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "story_chapters", schema = "pic")
class StoryChapter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    val story: Story,

    @Column(name = "chapter_order")
    val chapterOrder: Int = 0,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    var updatedBy: String? = null
)
