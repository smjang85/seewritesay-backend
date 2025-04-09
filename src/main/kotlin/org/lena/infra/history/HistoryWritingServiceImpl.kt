package org.lena.infra.history

import org.lena.api.dto.history.HistoryWritingResponseDto
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import org.lena.domain.history.entity.HistoryWriting
import org.lena.domain.history.repository.HistoryWritingRepository
import org.lena.domain.history.service.HistoryWritingService
import org.lena.domain.image.service.CategoryService
import org.springframework.stereotype.Service
import kotlin.String


@Service
class HistoryWritingServiceImpl(
    private val historyWritingRepository: HistoryWritingRepository,
    private val categoryService: CategoryService,
) : HistoryWritingService {

    override fun getHistory(user: User, imageId: Long?): List<HistoryWritingResponseDto> {
        val histories = if (imageId != null) {
            historyWritingRepository.findAllByUserIdAndImageId(user.id!!, imageId)
        } else {
            historyWritingRepository.findAllByUserId(user.id!!)
        }

        return histories.map {
            val image = it.image
            HistoryWritingResponseDto(
                id = it.id!!,
                imageId = image.id,
                imagePath = image.path,
                imageName = image.name,
                imageDescription = image.description,
                sentence = it.sentence,
                categoryId = image.categoryId,
                categoryName = it.category ?: "Unknown Category",
                createdAt = it.createdAt
            )
        }
    }

    override fun saveHistory(user: User, image: Image, sentence: String): HistoryWritingResponseDto {
        val category = categoryService.findById(image.categoryId)

        val entity = HistoryWriting(
            user = user,
            image = image,
            sentence = sentence,
            category = category?.name,
            createdBy = user.id.toString()
        )
        val saved = historyWritingRepository.save(entity)

        return HistoryWritingResponseDto(
            id = saved.id!!,
            imageId = image.id,
            imagePath = image.path,
            imageName = image.name,
            imageDescription = image.description,
            sentence = saved.sentence,
            categoryId = image.categoryId,
            categoryName = category!!.name,
            createdAt = saved.createdAt
        )
    }

    override fun getUserHistoryWithCategory(user: User): List<HistoryWritingResponseDto> {
        val historyList = historyWritingRepository.findAllByUserId(user.id)
        return historyList.map { history ->
            val image = history.image
            val categoryName = categoryService.findNameById(image.categoryId) ?: "Unknown Category"

            HistoryWritingResponseDto(
                id = history.id!!,
                imageId = image.id,
                imagePath = image.path,
                imageName = image.name,
                imageDescription = image.description,
                sentence = history.sentence,
                categoryId = image.categoryId,
                categoryName = categoryName,
                createdAt = history.createdAt
            )
        }
    }
}
