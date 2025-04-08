package org.lena.domain.writinghistory.entity

import jakarta.persistence.*
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import java.time.LocalDateTime

@Entity
@Table(name = "writing_history", schema = "pic")
class WritingHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User = User(), // 기본 생성자 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    val image: Image = Image(), // 기본 생성자 추가

    @Column(name = "sentence", nullable = false)
    val sentence: String = "", // 기본값 추가

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    var updatedBy: String? = null
) {
    // 기본 생성자를 제공하기 위해서 빈 생성자 추가
    constructor() : this(
        id = null,
        user = User(),
        image = Image(),
        sentence = "",
        createdAt = LocalDateTime.now(),
        createdBy = null,
        updatedAt = null,
        updatedBy = null
    )
}
