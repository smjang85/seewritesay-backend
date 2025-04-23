package org.lena.domain.user.service

import org.lena.domain.user.entity.User

interface UserService {
    fun findById(id: Long): User
    fun findByEmail(email: String): User?
    fun registerOrUpdate(email: String): User
    fun deleteUserById(userId: Long)
}
