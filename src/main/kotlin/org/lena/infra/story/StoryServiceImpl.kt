package org.lena.infra.story

import mu.KLogging
import org.lena.api.dto.story.StoryListResponseDto
import org.lena.api.dto.story.StoryResponseDto
import org.lena.domain.story.entity.Story
import org.lena.domain.story.entity.StoryTranslation
import org.lena.domain.story.enums.LanguageCode
import org.lena.domain.story.repository.StoryRepository
import org.lena.domain.story.repository.StoryTranslationRepository
import org.lena.domain.story.service.StoryService
import org.lena.domain.story.util.StoryUtils
import org.lena.domain.story.entity.toDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class StoryServiceImpl(
    private val storyRepository: StoryRepository,
    private val storyTranslationRepository: StoryTranslationRepository
) : StoryService {

    companion object : KLogging()

    @Transactional(readOnly = true)
    override fun getAllStories(language: LanguageCode): List<StoryListResponseDto> {
        logger.debug("📘 getAllStories(language = {})", language.code)
        val rawResults = storyRepository.findAllWithTranslation(language.code)

        return rawResults.map {
            StoryListResponseDto(
                id = (it[0] as Number).toLong(),
                imagePath = it[1] as? String,
                title = it[2] as? String ?: "",
                createdAt = (it[3] as Timestamp).toLocalDateTime(),
                createdBy = it.getOrNull(4) as? String
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getStoryById(id: Long, language: LanguageCode): StoryResponseDto {
        val langCode = language.code
        logger.debug("📖 getStoryById(id = {}, lang = {})", id, langCode)

        return if (isMixedLanguage(langCode)) {
            getMixedLanguageStory(id, langCode)
        } else {
            getSingleLanguageStory(id, langCode)
        }
    }

    override fun saveStory(story: Story): Story = storyRepository.save(story)

    private fun isMixedLanguage(code: String): Boolean = code.contains("_")

    private fun getMixedLanguageStory(id: Long, code: String): StoryResponseDto {
        logger.info("🔎 혼합 언어 번역 조회: storyId=$id, code=$code")

        val existing = storyTranslationRepository.findByStoryIdAndLanguageCode(id, code)
            ?: throw IllegalArgumentException("혼합 언어 번역이 존재하지 않습니다. ")
        return existing.toDto()
    }

    @Transactional(propagation = REQUIRES_NEW)
    override fun saveMixedLanguageStory(id: Long, code: String): StoryResponseDto {
        val languages = code.split("_").map(String::trim)
        // 혼합 언어는 정확히 2개만 허용
        if (languages.size != 2) {
            throw IllegalArgumentException("혼합 언어는 2개까지만 허용됩니다: $code")
        }

        val existing = storyTranslationRepository.findByStoryIdAndLanguageCode(id, code)
        if (existing != null) {
            logger.debug("🚫 [${id}] $code 혼합 번역 이미 존재. 저장 생략")
            return existing.toDto()
        }


        val (lang1, lang2) = code.split("_").map(String::trim)

        val trans1 = getTranslationFromRaw(id, lang1)
        val trans2 = getTranslationFromRaw(id, lang2)

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

    private fun getSingleLanguageStory(id: Long, code: String): StoryResponseDto {
        logger.debug("getSingleLanguageStory id= $id, code=$code")
        val translation = getTranslationFromRaw(id, code)
        return translation.toDto()
    }

    private fun getTranslationFromRaw(storyId: Long, language: String): StoryTranslation {
        val result = storyRepository.findByIdWithTranslation(storyId, language)
            ?: throw IllegalArgumentException("$language 번역이 존재하지 않습니다.")

        val row = result as? Array<Any>
            ?: throw IllegalStateException("Unexpected native query result type")

        return StoryTranslation(
            id = (row[0] as Number).toLong(),
            story = Story(id = (row[1] as Number).toLong()),
            languageCode = row[2] as String,
            title = row[3] as String,
            content = row[4] as String,
            createdAt = (row[5] as? Timestamp)?.toLocalDateTime() ?: throw IllegalStateException("createdAt 누락"),
            createdBy = row[6] as? String
        )
    }
}
