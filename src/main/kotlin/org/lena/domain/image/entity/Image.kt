package org.lena.domain.image.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "images", schema = "pic")
data class Image(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String = "",

    @Column(nullable = false)
    val path: String = "",

    @Column(name = "category_id", nullable = false)
    val categoryId: Long = 0,

    val description: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by")
    val createdBy: String? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    val updatedBy: String? = null
) {
    constructor() : this(0, "", "", 0)
}
