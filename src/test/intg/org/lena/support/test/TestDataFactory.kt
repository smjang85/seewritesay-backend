package org.lena.support.test

import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository

object TestDataFactory {

    fun createUser(userRepository: UserRepository, email: String = "test@example.com", name: String = "테스트유저"): User {
        return userRepository.save(User(email = email, name = name))
    }

    fun createImage(imageRepository: ImageRepository): Image {
        return imageRepository.save(
            Image(
                name = "샘플 이미지",
                path = "/images/sample.jpg",
                description = "샘플 설명",
                categoryId = 1
            )
        )
    }
}
