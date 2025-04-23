package org.lena.infra.user

import mu.KotlinLogging
import org.lena.api.dto.user.UserProfileResponseDto
import org.lena.config.properties.user.NicknameProperties
import org.lena.domain.user.enums.AgeGroup
import org.lena.domain.user.repository.UserRepository
import org.lena.domain.user.service.UserProfileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserProfileServiceImpl(
    private val userRepository: UserRepository,
    private val nicknameProperties: NicknameProperties
) : UserProfileService {

    private val logger = KotlinLogging.logger {}

    override fun isNicknameAvailable(nickname: String): Boolean {
        return !userRepository.existsByNickname(nickname)
    }

    override fun generateUniqueNickname(): String {
        val candidates = mutableSetOf<String>()

        repeat(10) {
            candidates.add(generateRandomNickname())
        }

        val takenNicknames = userRepository.findByNicknameIn(candidates)
            .mapNotNull { it.nickname }
            .toSet()

        val availableNickname = candidates.firstOrNull { it !in takenNicknames }

        return availableNickname ?: "user${System.currentTimeMillis() % 10000}"
    }

    private fun generateRandomNickname(): String {
        val formatters = listOf<(Boolean) -> String>(
            { isKorean -> "${randomAdjective(isKorean)}${randomAnimal(isKorean)}${randomNumber()}" },
            { isKorean -> "${randomAnimal(isKorean)}_${randomNumber()}" },
            { isKorean -> "${randomAdjective(isKorean)}${randomAnimal(isKorean)}" }
        )

        val isKorean = (0..1).random() == 0
        val formatter = formatters.random()
        return formatter(isKorean)
    }

    private fun randomAdjective(isKorean: Boolean): String =
        if (isKorean) nicknameProperties.korean.adjectives.random()
        else nicknameProperties.english.adjectives.random()

    private fun randomAnimal(isKorean: Boolean): String =
        if (isKorean) nicknameProperties.korean.animals.random()
        else nicknameProperties.english.animals.random()

    private fun randomNumber(): Int = (100..999).random()

    @Transactional
    override fun updateProfile(userId: Long, nickname: String, avatar: String, ageGroup: AgeGroup) {
        val user = userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("User not found: $userId")
        }

        if (ageGroup == AgeGroup.UNKNOWN) {
            throw IllegalArgumentException("유효하지 않은 연령대입니다.")
        }

        user.nickname = nickname
        user.avatar = avatar
        user.ageGroup = ageGroup.code  // 💡 DB에는 문자열 code 저장
        user.updatedAt = LocalDateTime.now()
        user.updatedBy = "user-$userId"

        userRepository.save(user)
    }

    override fun getCurrentUserProfile(userId: Long): UserProfileResponseDto {
        val user = userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("사용자를 찾을 수 없습니다: $userId")
        }

        return UserProfileResponseDto(
            nickname = user.nickname,
            avatar = user.avatar,
            ageGroup = user.ageGroup
        )
    }
}
