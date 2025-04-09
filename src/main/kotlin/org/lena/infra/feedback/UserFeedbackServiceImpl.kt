package org.lena.infra.feedback

import org.lena.domain.feedback.entity.UserFeedback
import org.lena.domain.feedback.repository.UserFeedbackRepository
import org.lena.domain.feedback.service.UserFeedbackService
import org.lena.domain.user.entity.User
import org.lena.domain.image.entity.Image
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserFeedbackServiceImpl(
    private val userFeedbackRepository: UserFeedbackRepository
) : UserFeedbackService {

    override fun getRemainingCount(user: User, image: Image): Int {
        return userFeedbackRepository.findByUserAndImage(user, image)?.remainingCount ?: 5
    }

    @Transactional
    override fun decrementFeedbackCount(user: User, image: Image) {
        val feedback = userFeedbackRepository.findByUserAndImage(user, image)
            ?: UserFeedback.of(user = user, image = image, remainingCount = 5)

        val current = feedback.remainingCount
        if (current > 0) {
            feedback.remainingCount = current - 1
            userFeedbackRepository.save(feedback)
        }
    }

    @Transactional
    override fun resetFeedbackCount(user: User, image: Image, count: Int) {
        val feedback = userFeedbackRepository.findByUserAndImage(user, image)
            ?: UserFeedback.of(user = user, image = image, remainingCount = count)

        feedback.remainingCount = count
        userFeedbackRepository.save(feedback)
    }
}
