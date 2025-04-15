package org.lena.infra.feedback

import mu.KLogging
import org.lena.api.dto.feedback.user.UserFeedbackResponseDto
import org.lena.domain.feedback.entity.UserFeedback
import org.lena.domain.feedback.repository.UserFeedbackRepository
import org.lena.domain.feedback.service.UserFeedbackService
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserFeedbackServiceImpl(
    private val userFeedbackRepository: UserFeedbackRepository,
    private val userService: UserService,
    private val imageService: ImageService,
) : UserFeedbackService {

    companion object : KLogging()

    override fun getRemainingCount(userId: Long, imageId: Long): UserFeedbackResponseDto {
        logger.debug("getRemainingCount start")

        val user = userService.findById(userId)
        logger.debug("getRemainingCount user: $user")
        val image = imageService.findById(imageId)
        logger.debug("getRemainingCount image: $image")

        val userFeedback = userFeedbackRepository.findByUserAndImage(user, image)
            ?: UserFeedback.of(
                user = user,
                image = image,
                writing_remaining_count = 30,
                reading_remaining_count = 2
            )

        logger.debug("getRemainingCount userFeedback: $userFeedback")
        return UserFeedbackResponseDto.fromEntity(userFeedback)
    }

    @Transactional
    override fun decrementWritingFeedbackCount(userId: Long, imageId: Long) {
        val user = userService.findById(userId)
        val image = imageService.findById(imageId)

        val userFeedback = userFeedbackRepository.findByUserAndImage(user, image)
            ?: UserFeedback.of(
                user = user,
                image = image,
                writing_remaining_count = 30,
                reading_remaining_count = 2
            )

        if (userFeedback.writing_remaining_count > 0) {
            userFeedback.writing_remaining_count -= 1
            userFeedbackRepository.save(userFeedback)
        }
    }
}
