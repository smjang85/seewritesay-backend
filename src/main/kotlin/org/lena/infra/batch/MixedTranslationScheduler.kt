package org.lena.infra.batch

import mu.KotlinLogging
import org.lena.domain.story.enums.LanguageCode
import org.lena.domain.story.repository.StoryRepository
import org.lena.domain.story.repository.StoryTranslationRepository
import org.lena.infra.story.StoryServiceImpl
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MixedTranslationScheduler(
    private val storyRepository: StoryRepository,
    private val storyTranslationRepository: StoryTranslationRepository,
    private val storyServiceImpl: StoryServiceImpl
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "\${story.mixed-translation.schedule}")
    @Transactional
    fun generateMissingMixedTranslations() {
        logger.info("🔄 혼합 번역 자동 생성 시작")

        val validPairs = getValidLanguagePairs()
        val allStories = storyRepository.findAll()

        for (story in allStories) {
            val storyId = story.id ?: continue
            val existingCodes = storyTranslationRepository.findCodesByStoryId(storyId).toSet()

            for (pair in validPairs) {
                val langParts = pair.split("_")
                if (langParts.size != 2) continue  // 보호

                if (!existingCodes.contains(pair) && hasPairBaseLangs(existingCodes, pair)) {
                    try {
                        storyServiceImpl.saveMixedLanguageStory(storyId, pair)
                        logger.info("✅ [$storyId] $pair 혼합 번역 생성 완료")
                    } catch (e: IllegalArgumentException) {
                        logger.warn("⚠️ [$storyId] $pair 생성 실패 - ${e.message}")
                    } catch (e: Exception) {
                        logger.error(e) { "❌ [$storyId] $pair 생성 중 오류 발생" }
                    }
                }
            }
        }

        logger.info("🏁 혼합 번역 자동 생성 완료")
    }

    private fun hasPairBaseLangs(existing: Set<String>, pair: String): Boolean {
        val (l1, l2) = pair.split("_")
        return existing.contains(l1) && existing.contains(l2)
    }

    /**
     * 가능한 모든 언어쌍 조합 반환 (자기 자신 제외)
     */
    fun getValidLanguagePairs(): List<String> {
        val codes = LanguageCode.allCodes()
        return codes.flatMap { lang1 ->
            codes.filter { it != lang1 }.map { lang2 -> "${lang1}_$lang2" }
        }
    }
}
