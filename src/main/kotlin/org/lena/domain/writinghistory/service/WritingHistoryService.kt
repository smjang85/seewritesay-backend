package org.lena.domain.writinghistory.service

import org.lena.domain.image.entity.Image
import org.lena.domain.user.entity.User
import org.lena.domain.writinghistory.entity.WritingHistory

interface WritingHistoryService {
    fun saveHistory(user: User, image: Image, sentence: String): WritingHistory
    fun getHistory(user: User, imageId: Long?): List<WritingHistory>
}