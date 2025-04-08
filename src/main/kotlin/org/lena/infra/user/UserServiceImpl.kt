package org.lena.infra.user

import mu.KotlinLogging
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

    private val logger = KotlinLogging.logger {}

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
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
        logger.info( "id : $id" );
        return userRepository.findById(id).orElseThrow { IllegalArgumentException("User not found: $id") }
    }

    override fun getUserSettings(customUserDto: CustomUserDto): UserSettingsResponseDto {
        val maxCount = 5
        val user = findById(customUserDto.id) // ✅ 여기만 수정
        val defaultImage = imageService.findById(1L)
        val remaining = userFeedbackService.getRemainingCount(user, defaultImage)

        return UserSettingsResponseDto(
            username = customUserDto.name,
            maxFeedbackCount = maxCount,
            remainingFeedbackCount = remaining
        )
    }
}
