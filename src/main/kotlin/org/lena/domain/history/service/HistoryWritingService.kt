package org.lena.domain.history.service

import org.lena.api.dto.history.HistoryWritingRequestDto
import org.lena.api.dto.history.HistoryWritingResponseDto
import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User

interface HistoryWritingService {
    fun getHistory(userId: Long, imageId: Long?): List<HistoryWritingResponseDto>
    fun deleteHistoryById(userId: Long, historyId: Long)
    fun getUserHistoryWithCategory(userId: Long): List<HistoryWritingResponseDto>
    fun saveHistory(userId: Long, imageId: Long, sentence: String, grade: String): HistoryWritingResponseDto
}