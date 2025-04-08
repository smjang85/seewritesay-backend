package org.lena.api.controller.writinghistory

import mu.KLogging
import org.lena.api.dto.user.CustomUserDto
import org.lena.api.dto.writinghistory.WritingHistoryRequest
import org.lena.api.dto.writinghistory.WritingHistoryResponse
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.service.UserService
import org.lena.domain.writinghistory.service.WritingHistoryService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/writing/history")
class WritingHistoryController(
    private val userService: UserService,
    private val imageService: ImageService,
    private val writingHistoryService: WritingHistoryService
) {
    companion object : KLogging()

    @GetMapping
    fun getHistory(
        @RequestParam(required = false) imageId: Long?,
        @AuthenticationPrincipal user: CustomUserDto?
    ): ResponseEntity<List<WritingHistoryResponse>> {
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")
        val foundUser = userService.findById(user.id)
        val historyList = writingHistoryService.getHistory(foundUser, imageId)

        val response = historyList.map {
            WritingHistoryResponse(
                id = it.id!!,
                imageId = it.image.id,
                imagePath = it.image.path,
                imageName = it.image.name,
                imageDescription = it.image.description,
                sentence = it.sentence,
                createdAt = it.createdAt
            )
        }


        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun saveHistory(
        @RequestBody request: WritingHistoryRequest,
        @AuthenticationPrincipal user: CustomUserDto?
    ): ResponseEntity<WritingHistoryResponse> {
        if (user == null) throw RuntimeException("❌ 사용자 정보가 없습니다.")

        val foundUser = userService.findById(user.id)
        val image = imageService.findById(request.imageId)

        val saved = writingHistoryService.saveHistory(foundUser, image, request.sentence)

        val response = WritingHistoryResponse(
            id = saved.id!!,
            imageId = saved.image.id,
            imagePath = saved.image.path,
            imageName = saved.image.name,
            imageDescription = saved.image.description,
            sentence = saved.sentence,
            createdAt = saved.createdAt
        )

        return ResponseEntity.ok(response)
    }
}
