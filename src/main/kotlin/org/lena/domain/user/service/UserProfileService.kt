package org.lena.domain.user.service

import org.lena.api.dto.user.UserProfileResponseDto
import org.lena.domain.user.entity.User
import org.lena.domain.user.enums.AgeGroup

interface UserProfileService {
    fun isNicknameAvailable(nickname: String): Boolean
    fun generateUniqueNickname(): String
    fun updateProfile(userId: Long, nickname: String, avatar: String, ageGroup: AgeGroup)
    fun getCurrentUserProfile(userId: Long): UserProfileResponseDto
}
