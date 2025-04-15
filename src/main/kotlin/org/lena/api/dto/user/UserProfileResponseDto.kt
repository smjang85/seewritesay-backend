package org.lena.api.dto.user

data class UserProfileResponseDto(
    val nickname: String?,
    val avatar: String?,
    val ageGroup: String?
)

data class NicknameAvailabilityResponseDto(
    val available: Boolean
)

data class RandomNicknameResponseDto(
    val nickname: String
)