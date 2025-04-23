package org.lena.api.controller.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import mu.KLogging
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.user.UserFeedbackRequestDto
import org.lena.api.dto.user.UserFeedbackResponseDto
import org.lena.api.dto.user.UserFeedbackResponseDto.Companion.fromEntity
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.user.service.UserFeedbackService
import org.lena.domain.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/feedback")
@Tag(name = "사용자 피드백", description = "피드백 횟수 관리 API")
class UserFeedbackController(
    private val userService: UserService,
    private val userFeedbackService: UserFeedbackService,
) {
    companion object : KLogging()

    @GetMapping
    @Operation(summary = "남은 피드백 횟수 조회", description = "특정 이미지에 대해 사용자에게 남은 피드백 횟수를 조회합니다.")
    fun getFeedbackRemainingCount(
        @RequestParam imageId: Long,
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<UserFeedbackResponseDto>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        val userId = user.id
        logger.debug { "➡️ [GET] Remaining Count 요청 | userId=$userId, imageId=$imageId" }

        val userFeedback = userService.findById(userId).let { fromEntity(it) }

        logger.debug { "✅ Remaining Count 결과 | writing=${userFeedback.writingRemainingCount}, reading=${userFeedback.readingRemainingCount}" }

        return ResponseEntity.ok(ApiResponse.success(userFeedback, "남은 피드백 횟수 조회 성공"))
    }

    @PostMapping("/writing/decrement")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "피드백 횟수 차감", description = "해당 이미지에 대한 사용자의 작문 피드백 횟수를 1 감소시킵니다.")
    fun decrementWritingFeedbackCount(
        @CurrentUser user: CustomUserPrincipal?
    ) {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "↘️ [POST] Decrement Feedback | userId=${user.id}" }

        userFeedbackService.decrementWritingFeedbackCount(user.id)
    }

    @PostMapping("/reading/decrement")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "피드백 횟수 차감", description = "해당 이미지에 대한 사용자의 작문 피드백 횟수를 1 감소시킵니다.")
    fun decrementReadingFeedbackCount(
        @CurrentUser user: CustomUserPrincipal?
    ) {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "↘️ [POST] Decrement Feedback | userId=${user.id}" }

        userFeedbackService.decrementReadingFeedbackCount(user.id)
    }

}