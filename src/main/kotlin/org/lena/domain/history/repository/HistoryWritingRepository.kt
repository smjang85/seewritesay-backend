package org.lena.domain.history.repository


import org.lena.domain.history.entity.HistoryWriting
import org.springframework.data.jpa.repository.JpaRepository

interface HistoryWritingRepository : JpaRepository<HistoryWriting, Long> {
    fun findAllByUserId(userId: Long): List<HistoryWriting>
    fun findAllByUserIdAndImageId(userId: Long, imageId: Long): List<HistoryWriting>
}
