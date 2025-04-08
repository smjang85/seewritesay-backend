package org.lena.domain.feedback.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_feedback", schema = "pic",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "image_id"])]
)
class UserFeedback(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    var image: Image = Image(),

    @Column(name = "remaining_count")
    var remainingCount: Int? = 0,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    val updatedBy: String? = null

) {
    constructor() : this(
        0,
        User(),
        Image(),
        0,
        LocalDateTime.now(),
        null,
        null,
        null
    )
}
