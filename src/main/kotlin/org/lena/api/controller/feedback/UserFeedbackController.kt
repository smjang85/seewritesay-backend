package org.lena.api.controller.feedback

import jakarta.validation.Valid
import mu.KLogging
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.UserFeedbackRequestDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.feedback.service.UserFeedbackService
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.dto.feedback.UserFeedbackResponseDto
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/v1/user/feedback")
@Tag(name = "사용자 피드백", description = "피드백 횟수 관리 API")
class UserFeedbackController(
    private val userFeedbackService: UserFeedbackService,
) {
    companion object : KLogging()

    @GetMapping
    @Operation(summary = "남은 피드백 횟수 조회", description = "특정 이미지에 대해 사용자에게 남은 피드백 횟수를 조회합니다.")
    fun getWritingRemainingCount(
        @RequestBody @Valid request: UserFeedbackRequestDto,
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<UserFeedbackResponseDto>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "➡️ [GET] Remaining Count | userId=${user.id}, imageId=$request.imageId" }


        val userFeedbackResponseDto = userFeedbackService.getRemainingCount(user.id, request.imageId)
        logger.debug { "➡️ [GET] Remaining Count | count={${userFeedbackResponseDto.writingRemainingCount}" }
        logger.debug { "➡️ [GET] Remaining Count | count={${userFeedbackResponseDto.readingRemainingCount}" }

        return ResponseEntity.ok(ApiResponse.success(userFeedbackResponseDto, "남은 피드백 횟수 조회 성공"))
    }

    @PostMapping("/writing/decrement")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "피드백 횟수 차감", description = "해당 이미지에 대한 사용자의 작문 피드백 횟수를 1 감소시킵니다.")
    fun decrementWritingFeedbackCount(
        @RequestBody @Valid request: UserFeedbackRequestDto,
        @CurrentUser user: CustomUserPrincipal?
    ) {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "↘️ [POST] Decrement Feedback | userId=${user.id}, imageId=${request.imageId}" }

        userFeedbackService.decrementWritingFeedbackCount(user.id, request.imageId)
    }

}
