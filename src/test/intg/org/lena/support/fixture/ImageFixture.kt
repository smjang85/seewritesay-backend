package org.lena.support.fixture

import org.lena.domain.image.entity.Image
import java.time.LocalDateTime

object ImageFixture {
    fun create(
        name: String = "샘플 이미지",
        path: String = "/images/sample.jpg",
        description: String = "샘플 설명",
        categoryId: Long = 1L,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Image {
        return Image(
            name = name,
            path = path,
            description = description,
            categoryId = categoryId,
            createdAt = createdAt
        )
    }
}
