package org.lena.infra.batch

import mu.KotlinLogging
import org.lena.config.properties.user.UserFeedbackResetProperties
import org.lena.domain.user.repository.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserFeedbackResetScheduler(
    private val userRepository: UserRepository,
    private val properties: UserFeedbackResetProperties,
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "\${user.feedback.reset.schedule}")
    @Transactional
    fun resetUserRemainingCounts() {
        logger.info("🕛 유저 피드백 카운트 초기화 시작")

        val updatedCount = userRepository.resetUserCounts(
            writing = properties.writingCount,
            reading = properties.readingCount
        )

        logger.info("✅ 총 $updatedCount 명 유저의 카운트를 초기화했습니다.")
    }
}