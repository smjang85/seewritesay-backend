package org.lena.infra.user

import org.lena.api.dto.user.CustomUserDto
import org.lena.api.dto.user.UserSettingsResponseDto
import org.lena.domain.feedback.service.UserFeedbackService
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.repository.UserRepository
import org.lena.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.lena.domain.user.entity.User
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userFeedbackService: UserFeedbackService,
    private val imageService: ImageService
) : UserService {

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun registerIfNotExists(email: String, name: String): User {
        TODO("Not yet implemented")
    }

    override fun updateLastLogin(user: User) {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun registerOrUpdate(email: String, name: String?): User {
        val user = userRepository.findByEmail(email)
        return if (user != null) {
            user.lastLoginAt = LocalDateTime.now()
            userRepository.save(user)
        } else {
            val newUser = User(
                email = email,
                name = name,
                lastLoginAt = LocalDateTime.now(),
                createdBy = "system"
            )
            userRepository.save(newUser)
        }
    }

    override fun findById(id: Long): User {
        return userRepository.findById(id).orElseThrow { IllegalArgumentException("User not found: $id") }
    }

    override fun getUserSettings(user: CustomUserDto): UserSettingsResponseDto {
        val maxCount = 5
        val entity = user.entity ?: throw IllegalStateException("User 엔티티가 없습니다.")
        val defaultImage = imageService.findById(1L);

        val remaining = userFeedbackService.getRemainingCount(entity, defaultImage)
        return UserSettingsResponseDto(
            username = user.name,
            maxFeedbackCount = maxCount,
            remainingFeedbackCount = remaining
        )
    }
}
