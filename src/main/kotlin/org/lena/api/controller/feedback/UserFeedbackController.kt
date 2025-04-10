package org.lena.api.controller.feedback

import jakarta.validation.Valid
import mu.KLogging
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.UserFeedbackRequestDto
import org.lena.api.dto.feedback.UserFeedbackResetRequestDto
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
class aUserFeedbackController(
    private val imageService: ImageService,
    private val userFeedbackService: UserFeedbackService,
    private val userService: UserService
) {
    companion object : KLogging()

    @GetMapping
    @Operation(summary = "남은 피드백 횟수 조회", description = "특정 이미지에 대해 사용자가 남은 피드백 횟수를 조회합니다.")
    fun getRemainingCount(
        @RequestParam imageId: Long,
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<UserFeedbackResponseDto>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "➡️ [GET] Remaining Count | userId=${user.id}, imageId=$imageId" }

        val foundUser = userService.findById(user.id)
        val image = imageService.findById(imageId)
        val userFeedbackResponseDto = userFeedbackService.getRemainingCount(foundUser, image)

        logger.debug { "➡️ [GET] Remaining Count | count={$userFeedbackResponseDto.count}" }

        return ResponseEntity.ok(ApiResponse.success(userFeedbackResponseDto, "남은 피드백 횟수 조회 성공"))
    }

    @PostMapping("/decrement")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "피드백 횟수 차감", description = "해당 이미지에 대한 사용자의 피드백 횟수를 1 감소시킵니다.")
    fun decrementFeedbackCount(
        @RequestBody @Valid request: UserFeedbackRequestDto,
        @CurrentUser user: CustomUserPrincipal?
    ) {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "↘️ [POST] Decrement Feedback | userId=${user.id}, imageId=${request.imageId}" }

        val foundUser = userService.findById(user.id)
        val image = imageService.findById(request.imageId)

        userFeedbackService.decrementFeedbackCount(foundUser, image)
        userFeedbackService.getRemainingCount(foundUser, image)
    }

    @PostMapping("/reset")
    @Operation(summary = "피드백 횟수 초기화", description = "사용자의 특정 이미지에 대한 피드백 횟수를 원하는 값으로 초기화합니다.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resetFeedbackCount(
        @RequestBody @Valid request: UserFeedbackResetRequestDto,
        @CurrentUser user: CustomUserPrincipal?
    ){
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "🔁 [POST] Reset Feedback | userId=${user.id}, imageId=${request.imageId}, count=${request.count}" }

        val foundUser = userService.findById(user.id)
        val image = imageService.findById(request.imageId)

        userFeedbackService.resetFeedbackCount(foundUser, image, request.count)
    }
}
