package org.lena.domain.feedback.service


import org.lena.domain.user.entity.User
import org.lena.domain.image.entity.Image

interface UserFeedbackService {
    fun getRemainingCount(user: User, image: Image): Int
    fun decrementFeedbackCount(user: User, image: Image)
    fun resetFeedbackCount(user: User, image: Image, count: Int = 5)
}
