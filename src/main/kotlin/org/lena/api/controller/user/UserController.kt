package org.lena.api.controller.user

import jakarta.validation.Valid
import mu.KotlinLogging
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.user.NicknameAvailabilityResponseDto
import org.lena.api.dto.user.RandomNicknameResponseDto
import org.lena.api.dto.user.UpdateProfileRequestDto
import org.lena.api.dto.user.UserProfileResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.user.enums.AgeGroup
import org.lena.domain.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {


    private val logger = KotlinLogging.logger {}

    @GetMapping("/current-user-profile")
    fun getCurrentUserProfile(@CurrentUser user: CustomUserPrincipal): ApiResponse<UserProfileResponseDto> {
        val profile = userService.getCurrentUserProfile(user.id)
        return ApiResponse.success(profile, "사용자 정보 조회 성공")
    }

    @GetMapping("/check-nickname")
    fun checkNickname(
        @RequestParam nickname: String,
        @CurrentUser user: CustomUserPrincipal
    ): ApiResponse<NicknameAvailabilityResponseDto> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug("checkNickname start")

        val available = userService.isNicknameAvailable(nickname)
        return ApiResponse.success(
            NicknameAvailabilityResponseDto(available),
            "닉네임 중복 확인 완료"
        )
    }

    @GetMapping("/generate-nickname")
    fun generateRandomNickname(@CurrentUser user: CustomUserPrincipal): ApiResponse<RandomNicknameResponseDto> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug("generateRandomNickname start")

        val nickname = userService.generateUniqueNickname()
        return ApiResponse.success(
            RandomNicknameResponseDto(nickname),
            "랜덤 닉네임 생성 완료"
        )
    }


    @PostMapping("/update-profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateProfile(
        @Valid @RequestBody request: UpdateProfileRequestDto,
        @CurrentUser user: CustomUserPrincipal
    ) {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.info("updateProfile start")

        userService.updateProfile(
            userId = user.id,
            nickname = request.nickname,
            avatar = request.avatar,
            ageGroup = AgeGroup.ofCode(request.ageGroup)
        )
    }
}