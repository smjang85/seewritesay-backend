package org.lena.domain.history.entity

import jakarta.persistence.*
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import java.time.LocalDateTime

@Entity
@Table(name = "history_writing", schema = "pic")
class HistoryWriting private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    val image: Image,

    @Column(nullable = false)
    var sentence: String,

    @Column(nullable = false)
    var grade: String,

    @Column(name = "category")
    val category: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    var updatedBy: String? = null
) {

    companion object {
        fun of(
            user: User,
            image: Image,
            sentence: String,
            grade: String,
            category: String? = null,
            createdBy: String? = null
        ): HistoryWriting {
            return HistoryWriting(
                user = user,
                image = image,
                sentence = sentence,
                grade = grade,
                category = category,
                createdBy = createdBy
            )
        }
    }

    // JPA 기본 생성자
    constructor() : this(
        id = null,
        user = User(),
        image = Image(),
        sentence = "",
        grade = "",
        category = null,
        createdAt = LocalDateTime.now(),
        createdBy = null,
        updatedAt = null,
        updatedBy = null
    )

    fun updateSentence(newSentence: String, newGrade: String, updatedBy: String) {
        if (this.sentence == newSentence) return
        this.grade = newGrade
        this.sentence = newSentence
        this.updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
    }

}
