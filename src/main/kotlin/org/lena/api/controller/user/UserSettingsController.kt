package org.lena.api.controller.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KLogging
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.dto.user.UserSettingsResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "사용자 정보 관련 API")
@RestController
@RequestMapping("/api/v1/user")
class UserSettingsController(
    private val userService: UserService
) {

    companion object : KLogging()

    @Operation(
        summary = "사용자 설정 조회",
        description = "로그인된 사용자의 설정 정보를 조회합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "설정 정보 조회 성공",
                content = [Content(schema = Schema(implementation = UserSettingsResponseDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 또는 사용자 정보 없음"
            )
        ]
    )
    @GetMapping("/settings")
    fun getUserSettings(@CurrentUser user: CustomUserPrincipal?): UserSettingsResponseDto {
        if (user == null) throw RuntimeException("사용자 정보가 없습니다.")
        logger.debug { "GET /user/settings | userId=${user.id}" }
        return userService.getUserSettings(user)
    }
}