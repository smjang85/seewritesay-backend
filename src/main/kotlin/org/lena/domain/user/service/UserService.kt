package org.lena.domain.user.service

import org.lena.api.dto.user.CustomUserDto
import org.lena.api.dto.user.UserSettingsResponseDto
import org.lena.domain.user.entity.User

interface UserService {
    fun findById(id: Long): User
    fun findByEmail(email: String): User?
    fun registerIfNotExists(email: String, name: String): User
    fun updateLastLogin(user: User)
    fun registerOrUpdate(email: String, name: String?): User
    fun getUserSettings(user: CustomUserDto): UserSettingsResponseDto
}
