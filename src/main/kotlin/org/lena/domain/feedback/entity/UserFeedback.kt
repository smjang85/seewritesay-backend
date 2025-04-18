package org.lena.domain.feedback.entity

import jakarta.persistence.*
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_feedback", schema = "pic",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "image_id"])]
)
class UserFeedback private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    var image: Image,

    @Column(name = "writing_remaining_count", nullable = false)
    var writing_remaining_count: Int,

    @Column(name = "reading_remaining_count", nullable = false)
    var reading_remaining_count: Int,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    val updatedBy: String? = null

) {
    // 정적 팩토리 메서드
    companion object {
        fun of(
            user: User,
            image: Image,
            writing_remaining_count: Int,
            reading_remaining_count: Int,
            createdBy: String? = null
        ): UserFeedback {
            return UserFeedback(
                user = user,
                image = image,
                writing_remaining_count = writing_remaining_count,
                reading_remaining_count = reading_remaining_count,
                createdBy = createdBy
            )
        }
    }

    // JPA 기본 생성자 (프록시용)
    constructor() : this(
        0,
        User(),
        Image(),
        0,
        0,
        LocalDateTime.now(),
        null,
        null,
        null
    )
}
