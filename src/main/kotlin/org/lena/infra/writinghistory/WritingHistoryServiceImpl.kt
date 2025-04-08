package org.lena.infra.writinghistory

import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import org.lena.domain.writinghistory.entity.WritingHistory
import org.lena.domain.writinghistory.repository.WritingHistoryRepository
import org.lena.domain.writinghistory.service.WritingHistoryService
import org.springframework.stereotype.Service


@Service
class WritingHistoryServiceImpl(
    private val writingHistoryRepository: WritingHistoryRepository
) : WritingHistoryService {

    override fun saveHistory(user: User, image: Image, sentence: String): WritingHistory {
        val entity = WritingHistory(
            user = user,
            image = image,
            sentence = sentence,
            createdBy = user.id.toString()
        )
        return writingHistoryRepository.save(entity)
    }

    override fun getHistory(user: User, imageId: Long?): List<WritingHistory> {
        return if (imageId != null) {
            writingHistoryRepository.findAllByUserIdAndImageId(user.id!!, imageId)
        } else {
            writingHistoryRepository.findAllByUserId(user.id!!)
        }
    }
}
