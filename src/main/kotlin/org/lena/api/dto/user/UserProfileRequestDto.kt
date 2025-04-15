package org.lena.api.dto.user
import jakarta.validation.constraints.NotBlank

data class UpdateProfileRequestDto(
    @field:NotBlank(message = "닉네임은 필수입니다.")
    val nickname: String,

    @field:NotBlank(message = "아바타는 필수입니다.")
    val avatar: String,

    @field:NotBlank(message = "연령대는 필수입니다.")
    val ageGroup: String
)
