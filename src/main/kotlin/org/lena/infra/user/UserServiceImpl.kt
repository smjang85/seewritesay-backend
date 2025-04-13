package org.lena.infra.user


import mu.KotlinLogging
import org.lena.domain.user.repository.UserRepository
import org.lena.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.lena.domain.user.entity.User
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
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
            val newUser = User.of(
                email = email,
                name = name,
                lastLoginAt = LocalDateTime.now(),
                createdBy = "system"
            )
            userRepository.save(newUser)
        }
    }

    override fun findById(id: Long): User {
        logger.debug( "id : $id" );
        return userRepository.findById(id).orElseThrow { IllegalArgumentException("User not found: $id") }
    }


}
