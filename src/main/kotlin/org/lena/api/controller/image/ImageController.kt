package org.lena.api.controller.image

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KLogging
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.image.ImageResponseDto
import org.lena.domain.image.service.ImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Image", description = "이미지 관련 API")
@RestController
@RequestMapping("/api/v1/images")
class ImageController(
    private val imageService: ImageService
) {
    companion object : KLogging()

    @Operation(
        summary = "전체 이미지 목록 조회",
        description = "DB에 저장된 전체 이미지 목록을 조회합니다.",
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "이미지 목록 조회 성공",
                content = [Content(schema = Schema(implementation = ImageResponseDto::class))]
            )
        ]
    )
    @GetMapping
    fun getAllImages(): ResponseEntity<ApiResponse<List<ImageResponseDto>>> {
        logger.debug { "GET /images - 전체 이미지 목록 요청" }

        val images = imageService.findAll()
        return ResponseEntity.ok(ApiResponse.success(images))
    }
}
