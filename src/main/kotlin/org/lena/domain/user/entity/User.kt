package org.lena.domain.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users", schema = "pic")
class User private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    val name: String? = null,

    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    val updatedBy: String? = null

) {
    companion object {
        fun of(
            email: String,
            name: String? = null,
            lastLoginAt: LocalDateTime? = null,
            createdBy: String? = null
        ): User {
            return User(
                email = email,
                name = name,
                lastLoginAt = lastLoginAt,
                createdBy = createdBy
            )
        }
    }

    // JPA 기본 생성자
    constructor() : this(
        id = 0,
        email = "",
        name = null,
        lastLoginAt = null,
        createdAt = LocalDateTime.now(),
        createdBy = null,
        updatedAt = null,
        updatedBy = null
    )
}
