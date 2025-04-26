package org.lena.infra.story

import mu.KLogging
import org.lena.api.dto.story.StoryListResponseDto
import org.lena.api.dto.story.StoryResponseDto
import org.lena.domain.story.entity.Story
import org.lena.domain.story.entity.StoryTranslation
import org.lena.domain.story.enums.LanguageCode
import org.lena.domain.story.enums.StoryType
import org.lena.domain.story.projection.StoryListProjection
import org.lena.domain.story.projection.StoryTranslationProjection
import org.lena.domain.story.repository.StoryRepository
import org.lena.domain.story.repository.StoryTranslationRepository
import org.lena.domain.story.service.StoryService
import org.lena.domain.story.util.StoryUtils
import org.lena.domain.story.entity.toDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class StoryServiceImpl(
    private val storyRepository: StoryRepository,
    private val storyTranslationRepository: StoryTranslationRepository
) : StoryService {

    companion object : KLogging()

    @Transactional(readOnly = true)
    override fun getAllStories(language: LanguageCode): List<StoryListResponseDto> {
        logger.debug("📘 getAllStories(language = {})", language.code)
        return storyRepository.findStoryListProjectionNative(language.code)
            .map { it.toStoryListResponseDto() }
    }

    @Transactional(readOnly = true)
    override fun getStoriesByType(language: LanguageCode, type: String): List<StoryListResponseDto> {
        val storyType = StoryType.fromCode(type)
        return storyRepository.findByType(storyType)
            .map { StoryListResponseDto.fromEntity(it, language) }
    }

    @Transactional(readOnly = true)
    override fun getStoryById(id: Long, language: LanguageCode, chapterId: Long?): StoryResponseDto {
        val langCode = language.code
        logger.debug("📖 getStoryById(id = {}, lang = {}, chapterId = {})", id, langCode, chapterId)

        return if (isMixedLanguage(langCode)) {
            getMixedLanguageStory(id, langCode)
        } else {
            getSingleLanguageContent(id, langCode, chapterId)
        }
    }

    override fun saveStory(story: Story): Story = storyRepository.save(story)

    private fun isMixedLanguage(code: String): Boolean = code.contains("_")

    private fun getMixedLanguageStory(id: Long, code: String): StoryResponseDto {
        logger.info("🔎 혼합 언어 번역 조회: storyId=$id, code=$code")
        return storyTranslationRepository.findByStoryIdAndLanguageCode(id, code)
            ?.toDto()
            ?: throw IllegalArgumentException("혼합 언어 번역이 존재하지 않습니다.")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun saveMixedLanguageStory(id: Long, code: String): StoryResponseDto {
        val languages = code.split("_").map(String::trim)
        require(languages.size == 2) { "혼합 언어는 2개까지만 허용됩니다: $code" }

        val existing = storyTranslationRepository.findByStoryIdAndLanguageCode(id, code)
        if (existing != null) {
            logger.debug("🚫 [${id}] $code 혼합 번역 이미 존재. 저장 생략")
            return existing.toDto()
        }

        val (lang1, lang2) = languages
        val trans1 = findTranslationEntity(id, lang1)
        val trans2 = findTranslationEntity(id, lang2)

        val mixedContent = StoryUtils.sentenceMix(trans1.content, trans2.content)

        val newTranslation = StoryTranslation(
            story = trans1.story,
            languageCode = code,
            title = trans1.title,
            content = mixedContent,
            createdBy = "system"
        )
        storyTranslationRepository.save(newTranslation)

        return newTranslation.toDto()
    }

    private fun getSingleLanguageContent(storyId: Long, language: String, chapterId: Long?): StoryResponseDto {
        return findTranslationEntity(storyId, language, chapterId).toDto()
    }

    private fun findTranslationEntity(storyId: Long, language: String, chapterId: Long? = null): StoryTranslation {
        return storyRepository.findTranslation(storyId, language, chapterId)
            .orElseThrow { IllegalArgumentException("번역이 존재하지 않습니다.") }
            .toEntity()
    }

    private fun StoryListProjection.toStoryListResponseDto(): StoryListResponseDto {
        return StoryListResponseDto(
            id = getId(),
            imagePath = getImagePath(),
            type = getType() ?: "",
            title = getTitle(),
            createdAt = getCreatedAt(),
            createdBy = getCreatedBy()
        )
    }

    private fun StoryTranslationProjection.toEntity(): StoryTranslation {
        return StoryTranslation(
            id = getId(),
            story = Story(id = getStoryId()),
            languageCode = getLanguageCode(),
            title = getTitle(),
            content = getContent(),
            createdAt = getCreatedAt(),
            createdBy = getCreatedBy()
        )
    }
}
