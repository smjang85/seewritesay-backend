package org.lena.api.controller.story

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KLogging
import org.lena.api.common.annotation.CurrentUser
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.story.ChapterDto
import org.lena.api.dto.story.StoryListResponseDto
import org.lena.api.dto.story.StoryResponseDto
import org.lena.config.security.CustomUserPrincipal
import org.lena.domain.story.enums.LanguageCode
import org.lena.domain.story.service.StoryChapterService
import org.lena.domain.story.service.StoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Story", description = "아이들을 위한 스토리 관련 API")
@RestController
@RequestMapping("/api/v1/story")
class StoryController(
    private val storyService: StoryService,
    private val storyChapterService: StoryChapterService
) {

    companion object : KLogging()

    @Operation(summary = "전체 스토리 목록 조회", description = "언어와 유형에 맞는 스토리 목록을 조회합니다.")
    @GetMapping
    fun getAllStories(
        @Parameter(description = "언어 코드 (예: ko, en, en_ko)")
        @RequestParam(name = "lang", defaultValue = "KO") lang: String,

        @Parameter(description = "스토리 유형 코드 (S: 단편, L: 장편)")
        @RequestParam(name = "type", required = false) type: String?,

        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<List<StoryListResponseDto>>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug("📚 getAllStories - lang: {}, type: {}", lang, type)

        val language = LanguageCode.from(lang)
        val stories = if (type.isNullOrBlank()) {
            storyService.getAllStories(language)
        } else {
            storyService.getStoriesByType(language, type)
        }

        logger.debug("📚 조회된 stories: {}", stories)
        return ResponseEntity.ok(ApiResponse.success(stories, "스토리 목록 조회 성공"))
    }

    @Operation(summary = "챕터 목록 조회", description = "스토리 ID와 언어에 맞는 챕터 목록을 조회합니다.")
    @GetMapping("/{storyId}/chapters")
    fun getChapters(
        @PathVariable storyId: Long,
        @RequestParam(name = "lang", defaultValue = "ko") lang: String,
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<List<ChapterDto>>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug("📖 getChapters - storyId: {}, lang: {}", storyId, lang)

        val chapters = storyChapterService.getChapters(storyId, lang)

        logger.debug("📖 조회된 chapters: {}", chapters)
        return ResponseEntity.ok(ApiResponse.success(chapters, "챕터 목록 조회 성공"))
    }

    @Operation(summary = "스토리 단건 조회", description = "스토리 ID, 챕터 ID, 언어에 맞는 스토리를 조회합니다.")
    @GetMapping("/{id}")
    fun getStoryById(
        @PathVariable id: Long,
        @RequestParam(name = "lang", defaultValue = "KO") lang: String,
        @RequestParam(name = "chapterId", required = false) chapterId: Long?,
        @CurrentUser user: CustomUserPrincipal?
    ): ResponseEntity<ApiResponse<StoryResponseDto>> {
        requireNotNull(user) { "사용자 정보가 없습니다." }
        logger.debug("📖 getStoryById - id: {}, lang: {}, chapterId: {}", id, lang, chapterId)

        val language = LanguageCode.from(lang)
        val story = storyService.getStoryById(id, language, chapterId)

        logger.debug("📖 조회된 story: {}", story)
        return ResponseEntity.ok(ApiResponse.success(story, "스토리 조회 성공"))
    }
}
