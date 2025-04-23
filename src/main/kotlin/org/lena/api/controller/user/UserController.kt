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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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

    @DeleteMapping("/delete")
    fun deleteMyAccount(
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<Nothing>> {
        requireNotNull(user) { "인증된 사용자가 없습니다." }

        userService.deleteUserById(user.id)

        return ResponseEntity.ok(ApiResponse.success(message = "회원 탈퇴가 완료되었습니다."))
    }
}