package org.lena.domain.writinghistory.repository


import org.lena.domain.writinghistory.entity.WritingHistory
import org.springframework.data.jpa.repository.JpaRepository

interface WritingHistoryRepository : JpaRepository<WritingHistory, Long> {
    fun findAllByUserId(userId: Long): List<WritingHistory>
    fun findAllByUserIdAndImageId(userId: Long, imageId: Long): List<WritingHistory>
}
