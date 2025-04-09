package org.lena.domain.history.service

import org.lena.api.dto.history.HistoryWritingResponseDto
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User

interface HistoryWritingService {
    fun saveHistory(user: User, image: Image, sentence: String): HistoryWritingResponseDto
    fun getHistory(user: User, imageId: Long?): List<HistoryWritingResponseDto>
    fun getUserHistoryWithCategory(user: User): List<HistoryWritingResponseDto>
}