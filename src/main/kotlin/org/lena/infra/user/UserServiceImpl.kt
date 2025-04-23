package org.lena.infra.user

import mu.KotlinLogging
import org.lena.domain.history.repository.HistoryWritingRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.lena.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val historyWritingRepository: HistoryWritingRepository
) : UserService {

    private val logger = KotlinLogging.logger {}

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    override fun registerOrUpdate(email: String): User {
        val user = userRepository.findByEmail(email)
        return if (user != null) {
            user.lastLoginAt = LocalDateTime.now()
            userRepository.save(user)
        } else {
            val newUser = User.of(
                email = email,
                lastLoginAt = LocalDateTime.now(),
                writingRemainingCount = 30,
                readingRemainingCount = 5,
                createdBy = "system"
            )
            userRepository.save(newUser)
        }
    }

    override fun findById(id: Long): User {
        logger.debug("id : $id")
        return userRepository.findById(id).orElseThrow {
            IllegalArgumentException("User not found: $id")
        }
    }


    @Transactional
    override fun deleteUserById(userId: Long) {
        // 1. 사용자의 작성 히스토리 삭제
        historyWritingRepository.deleteByUserId(userId)


        // 3. 사용자 계정 삭제
        userRepository.deleteById(userId)
    }
}
