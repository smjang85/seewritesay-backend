package org.lena.domain.image.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "categories", schema = "pic")
data class Category(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String = "",

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    val updatedBy: String? = null
) {
    // JPA 기본 생성자
    constructor() : this(0, "")
}
