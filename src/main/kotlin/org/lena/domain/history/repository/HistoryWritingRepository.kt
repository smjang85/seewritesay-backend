package org.lena.domain.history.repository


import org.lena.domain.history.entity.HistoryWriting
import org.springframework.data.jpa.repository.JpaRepository
import org.lena.domain.user.entity.User
import org.lena.domain.image.entity.Image

interface HistoryWritingRepository : JpaRepository<HistoryWriting, Long> {
    fun findAllByUserId(userId: Long): List<HistoryWriting>
    fun findAllByUserIdAndImageId(userId: Long, imageId: Long): List<HistoryWriting>
    fun findByUserIdAndImageId(userId: Long, imageId: Long): HistoryWriting?
    fun findByIdAndUser(id: Long, user: User): HistoryWriting?
}
