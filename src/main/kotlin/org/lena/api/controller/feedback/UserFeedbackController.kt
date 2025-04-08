package org.lena.api.controller.feedback

import mu.KLogging
import org.lena.api.dto.feedback.UserFeedbackRequest
import org.lena.api.dto.feedback.UserFeedbackResetRequest
import org.lena.api.dto.user.CustomUserDto
import org.lena.domain.feedback.service.UserFeedbackService
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.entity.User
import org.lena.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/feedback")
class UserFeedbackController(
    private val imageService: ImageService,
    private val userFeedbackService: UserFeedbackService,
    private val userService: UserService

) {

    companion object : KLogging()

    @GetMapping
    fun getRemainingCount(
        @RequestParam imageId: Long,
        @AuthenticationPrincipal user: CustomUserDto?
    ): ResponseEntity<Map<String, Int>> {

        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        logger.info("user.id: $user.id");
        logger.info("imageId: $imageId");

        val user = userService.findById(user.id)
        val image = imageService.findById(imageId)
        val count = userFeedbackService.getRemainingCount(user, image)

        return ResponseEntity.ok(mapOf("remainingCount" to count))
    }

    @PostMapping("/decrement")
    fun decrementFeedbackCount(
        @RequestBody request: UserFeedbackRequest,
        @AuthenticationPrincipal user: CustomUserDto?
    ): ResponseEntity<Map<String, Int>> {

        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        val user = userService.findById(user.id)

        val image = imageService.findById(request.imageId)
        userFeedbackService.decrementFeedbackCount(user!!, image)
        val updated = userFeedbackService.getRemainingCount(user, image)
        return ResponseEntity.ok(mapOf("remainingCount" to updated))
    }

    @PostMapping("/reset")
    fun resetFeedbackCount(
        @RequestBody request: UserFeedbackResetRequest,
        @AuthenticationPrincipal user: CustomUserDto?
    ): ResponseEntity<Map<String, Int>> {

        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        val user = userService.findById(user.id)
        val image = imageService.findById(request.imageId)
        userFeedbackService.resetFeedbackCount(user, image, request.count)
        return ResponseEntity.ok(mapOf("remainingCount" to request.count))
    }
}