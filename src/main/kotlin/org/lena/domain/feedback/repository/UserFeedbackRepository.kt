package org.lena.domain.feedback.repository

import org.lena.domain.feedback.entity.UserFeedback
import org.lena.domain.user.entity.User
import org.lena.domain.image.entity.Image
import org.springframework.data.jpa.repository.JpaRepository

interface UserFeedbackRepository : JpaRepository<UserFeedback, Long> {
    fun findByUserAndImage(user: User, image: Image): UserFeedback?
    fun deleteByUserId(userId: Long)
}
