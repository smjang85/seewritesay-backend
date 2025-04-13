package org.lena.infra.feedback

import org.lena.api.dto.feedback.UserFeedbackResponseDto
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

    override fun getRemainingCount(userId: Long, imageId: Long): UserFeedbackResponseDto {
        val user = userService.findById(userId)
        val image = imageService.findById(imageId)

        val userFeedback = userFeedbackRepository.findByUserAndImage(user, image)
            ?: UserFeedback.of(
                user = user,
                image = image,
                writing_remaining_count = 30,
                reading_remaining_count = 2
            )

        return UserFeedbackResponseDto(
            writingRemainingCount = userFeedback.writing_remaining_count,
            readingRemainingCount = userFeedback.reading_remaining_count
        )
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
