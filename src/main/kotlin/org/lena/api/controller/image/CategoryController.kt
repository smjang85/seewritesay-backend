package org.lena.api.controller.image

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import mu.KLogging
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.image.CategoryRequestDto
import org.lena.api.dto.image.CategoryResponseDto
import org.lena.domain.image.entity.Category
import org.lena.domain.image.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Category", description = "이미지 카테고리 관련 API")
@RestController
@RequestMapping("/api/v1/images/categories")
class CategoryController(
    private val categoryService: CategoryService
) {
    companion object : KLogging()

    @Operation(
        summary = "카테고리 목록 조회",
        description = "전체 이미지 카테고리 목록을 조회합니다.",
        responses = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "카테고리 목록 조회 성공",
                content = [Content(schema = Schema(implementation = CategoryResponseDto::class))]
            )
        ]
    )
    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<CategoryResponseDto>>> {
        logger.debug { "GET /images/categories - 카테고리 전체 조회 요청" }

        val categories = categoryService.findAll()
            .map(CategoryResponseDto::from)

        return ResponseEntity.ok(ApiResponse.success(categories, "카테고리 목록 조회 성공"))
    }

    @Operation(
        summary = "카테고리 생성",
        description = "새로운 이미지 카테고리를 생성합니다.",
        requestBody = SwaggerRequestBody(
            required = true,
            description = "생성할 카테고리 정보",
            content = [Content(schema = Schema(implementation = CategoryRequestDto::class))]
        ),
        responses = [
            SwaggerApiResponse(
                responseCode = "201",
                description = "카테고리 생성 완료",
                content = [Content(schema = Schema(implementation = CategoryResponseDto::class))]
            )
        ]
    )
    @PostMapping
    fun create(
        @RequestBody @Valid request: CategoryRequestDto
    ): ResponseEntity<ApiResponse<CategoryResponseDto>> {
        logger.debug { "POST /images/categories - 카테고리 생성 요청: ${request.name}" }

        val saved = categoryService.save(request.name)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(CategoryResponseDto.from(saved), "카테고리 생성 완료"))
    }
}
