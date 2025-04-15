package org.lena.infra.history

import mu.KLogging
import org.lena.api.dto.history.HistoryWritingResponseDto
import org.lena.domain.history.entity.HistoryWriting
import org.lena.domain.history.repository.HistoryWritingRepository
import org.lena.domain.history.service.HistoryWritingService
import org.lena.domain.image.service.CategoryService
import org.lena.domain.image.service.ImageService
import org.lena.domain.user.service.UserService
import org.springframework.stereotype.Service

@Service
class HistoryWritingServiceImpl(
    private val historyWritingRepository: HistoryWritingRepository,
    private val userService: UserService,
    private val imageService: ImageService,
    private val categoryService: CategoryService
) : HistoryWritingService {

    companion object : KLogging()

    override fun getHistory(userId: Long, imageId: Long?): List<HistoryWritingResponseDto> {
        val historyList = if (imageId != null) {
            historyWritingRepository.findAllByUserIdAndImageId(userId, imageId)
        } else {
            historyWritingRepository.findAllByUserId(userId)
        }

        return historyList.map { history ->
            val categoryName = resolveCategoryName(history.image.categoryId, history.category)
            history.toDto(categoryName)
        }
    }

    override fun getUserHistoryWithCategory(userId: Long): List<HistoryWritingResponseDto> {
        val historyList = historyWritingRepository.findAllByUserId(userId)

        return historyList.map { history ->
            val categoryName = resolveCategoryName(history.image.categoryId, history.category)
            history.toDto(categoryName)
        }
    }

    override fun saveHistory(
        userId: Long,
        imageId: Long,
        sentence: String,
        grade: String
    ): HistoryWritingResponseDto {
        logger.debug("saveHistory start ")

        val user = userService.findById(userId)
        val image = imageService.findById(imageId)
        val category = categoryService.findById(image.categoryId)

        val existing = historyWritingRepository.findByUserIdAndImageId(userId, imageId)

        val saved = if (existing != null) {
            existing.updateSentence(sentence, grade, userId.toString())
            historyWritingRepository.save(existing)
        } else {
            val entity = HistoryWriting.of(
                user = user,
                image = image,
                sentence = sentence,
                grade = grade,
                category = category?.name,
                createdBy = userId.toString()
            )
            historyWritingRepository.save(entity)
        }

        logger.debug("saveHistory saved : $saved")
        return saved.toDto(category?.name ?: "Unknown Category")
    }

    override fun deleteHistoryById(userId: Long, historyId: Long) {
        val user = userService.findById(userId)
        val history = historyWritingRepository.findByIdAndUser(historyId, user)
            ?: throw IllegalArgumentException("해당 히스토리를 찾을 수 없습니다.")
        historyWritingRepository.delete(history)
    }

    private fun resolveCategoryName(categoryId: Long, fallback: String?): String {
        return categoryService.findNameById(categoryId) ?: fallback ?: "Unknown Category"
    }
}
