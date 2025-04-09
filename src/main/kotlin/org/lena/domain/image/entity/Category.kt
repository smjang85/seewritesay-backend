package org.lena.domain.image.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "categories", schema = "pic")
class Category private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String,

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
        fun of(name: String, createdBy: String? = null): Category {
            return Category(
                name = name,
                createdBy = createdBy
            )
        }
    }

    // JPA 기본 생성자
    constructor() : this(
        id = 0,
        name = "",
        createdAt = LocalDateTime.now(),
        createdBy = null,
        updatedAt = null,
        updatedBy = null
    )
}
