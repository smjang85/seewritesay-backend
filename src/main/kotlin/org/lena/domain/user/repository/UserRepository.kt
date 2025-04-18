package org.lena.domain.user.repository

import org.lena.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByNickname(nickname: String): Boolean
    fun findByNicknameIn(nicknames: Set<String>): List<User>
}
