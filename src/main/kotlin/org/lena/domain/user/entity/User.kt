package org.lena.domain.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users", schema = "pic")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    // 기본 생성자 추가
    constructor() : this(0, "", null, null, LocalDateTime.now(), null, null, null)
}