package org.lena.domain.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users", schema = "pic")
class User private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Column(length = 100)
    val name: String? = null,

    @Column(length = 255, unique = true)
    var nickname: String? = null,

    @Column(length = 25)
    var avatar: String? = null,

    @Column(name = "age_group", length = 1)
    var ageGroup: String? = null,

    @Column(name = "writing_remaining_count", nullable = false)
    var writingRemainingCount: Int = 30,

    @Column(name = "reading_remaining_count", nullable = false)
    var readingRemainingCount: Int = 5,

    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null,

    @Column(name = "created_at")
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
            email: String,
            name: String? = null,
            writingRemainingCount: Int,
            readingRemainingCount: Int,
            lastLoginAt: LocalDateTime? = null,
            createdBy: String? = null
        ): User {
            return User(
                email = email,
                name = name,
                writingRemainingCount = writingRemainingCount,
                readingRemainingCount = readingRemainingCount,
                lastLoginAt = lastLoginAt,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }

    }

    // JPA 기본 생성자
    constructor() : this(
        id = 0,
        email = "",
        name = null,
        nickname = null,
        avatar = null,
        ageGroup = null,
        writingRemainingCount = 30,
        readingRemainingCount = 5,
        lastLoginAt = null,
        createdAt = LocalDateTime.now(),
        createdBy = null,
        updatedAt = null,
        updatedBy = null
    )
}
