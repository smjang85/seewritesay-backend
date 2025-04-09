package org.lena.domain.user.service

import org.lena.config.security.CustomUserPrincipal
import org.lena.api.dto.user.UserSettingsResponseDto
import org.lena.domain.user.entity.User

interface UserService {
    fun findById(id: Long): User
    fun findByEmail(email: String): User?
    fun registerOrUpdate(email: String, name: String?): User
    fun getUserSettings(user: CustomUserPrincipal): UserSettingsResponseDto
}
