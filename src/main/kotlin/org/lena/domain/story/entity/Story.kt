package org.lena.domain.story.entity

import jakarta.persistence.*
import org.lena.domain.story.converter.StoryTypeConverter
import org.lena.domain.story.enums.StoryType
import java.time.LocalDateTime

@Entity
@Table(name = "stories", schema = "pic")
class Story(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "image_path")
    var imagePath: String? = null,

    @Column(name = "target_age_group")
    var targetAgeGroup: String? = null,

    @Column(name = "type", nullable = false, length = 1)
    @Convert(converter = StoryTypeConverter::class)
    var type: StoryType = StoryType.SHORT,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    val updatedBy: String? = null,

    @OneToMany(
        mappedBy = "story",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val translations: MutableList<StoryTranslation> = mutableListOf()
) {
    constructor() : this(0L)
}
