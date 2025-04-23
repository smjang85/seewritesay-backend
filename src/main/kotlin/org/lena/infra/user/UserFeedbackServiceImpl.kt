package org.lena.infra.user

import mu.KLogging
import org.lena.domain.user.service.UserFeedbackService
import org.lena.domain.user.repository.UserRepository
import org.lena.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserFeedbackServiceImpl(
    private val userRepository: UserRepository,
    private val userService: UserService,
) : UserFeedbackService {

    companion object : KLogging()

    @Transactional
    override fun decrementWritingFeedbackCount(userId: Long) {
        val user = userService.findById(userId)

        if (user.writingRemainingCount > 0) {
            user.writingRemainingCount -= 1
            userRepository.save(user)
        }
    }

    override fun decrementReadingFeedbackCount(userId: Long) {
        val user = userService.findById(userId)

        if (user.readingRemainingCount > 0) {
            user.readingRemainingCount -= 1
            userRepository.save(user)
        }
    }
}