package org.lena.api.controller.history

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import mu.KLogging
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.history.HistoryWritingRequestDto
import org.lena.api.dto.history.HistoryWritingResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.history.service.HistoryWritingService
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Writing History", description = "작문 히스토리 관련 API")
@RestController
@RequestMapping("/api/v1/history/writing")
class HistoryWritingController(
    private val userService: UserService,
    private val imageService: ImageService,
    private val writingHistoryService: HistoryWritingService
) {
    companion object : KLogging()

    @Operation(summary = "작문 히스토리 조회", description = "사용자의 전체 또는 특정 이미지에 대한 작문 히스토리를 조회합니다.")
    @GetMapping
    fun getHistory(
        @Parameter(description = "이미지 ID (선택)") @RequestParam(required = false) imageId: Long?,
        @CurrentUser user: CustomUserPrincipal?
    ): ApiResponse<List<HistoryWritingResponseDto>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "GET /history/writing | userId=${user.id}, imageId=$imageId" }

        val foundUser = userService.findById(user.id)
        val response = writingHistoryService.getHistory(foundUser, imageId)

        return ApiResponse.Companion.success(response, "작문 히스토리 조회 성공")
    }

    @Operation(summary = "작문 히스토리 저장", description = "사용자가 입력한 문장을 특정 이미지에 연결하여 작문 히스토리를 저장합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun saveHistory(
        @RequestBody @Valid request: HistoryWritingRequestDto,
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<HistoryWritingResponseDto>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "POST /history/writing | userId=${user.id}, imageId=${request.imageId}" }

        val foundUser = userService.findById(user.id)
        val image = imageService.findById(request.imageId)
        val response = writingHistoryService.saveHistory(foundUser, image, request.sentence)

        logger.debug { "POST /history/writing | response : ${response}" }

        return ResponseEntity.ok(ApiResponse.Companion.created(response, "작문 히스토리 저장 완료"))
    }

    @Operation(summary = "카테고리별 히스토리 조회", description = "사용자의 작문 히스토리를 카테고리 기준으로 그룹화하여 조회합니다.")
    @GetMapping("/with-category")
    fun getUserHistoryWithCategory(
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<List<HistoryWritingResponseDto>>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "GET /history/writing/with-category | userId=${user.id}" }

        val foundUser = userService.findById(user.id)
        val response = writingHistoryService.getUserHistoryWithCategory(foundUser)

        return ResponseEntity.ok(ApiResponse.Companion.success(response, "카테고리별 작문 히스토리 조회 성공"))
    }

    @Operation(summary = "작문 히스토리 삭제", description = "작문 히스토리 항목을 삭제합니다.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteHistory(
        @Parameter(description = "삭제할 히스토리 ID") @RequestParam id: Long,
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<Void>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug { "DELETE /history/writing | userId=${user.id}, historyId=$id" }

        val foundUser = userService.findById(user.id)
        writingHistoryService.deleteHistoryById(foundUser, id)

        return ResponseEntity.noContent().build()
    }
}