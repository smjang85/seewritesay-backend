package org.lena.domain.image.service

import mu.KotlinLogging
import org.lena.api.dto.image.ImageResponseDto
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.CategoryRepository
import org.lena.domain.image.repository.ImageRepository
import org.springframework.stereotype.Service

@Service
class ImageServiceImpl(
    private val imageRepository: ImageRepository,
    private val categoryRepository: CategoryRepository
) : ImageService {

    private val logger = KotlinLogging.logger {}

    override fun getDescriptionByImageId(imageId: Long): String {
        val image: Image = imageRepository.findById(imageId)
            .orElseThrow { IllegalStateException("존재하지 않는 이미지 ID입니다: $imageId") }

        return image.description ?: throw IllegalStateException("이미지 설명이 비어 있습니다: $imageId")
    }

    override fun findAll(): List<ImageResponseDto> {
        val categories = categoryRepository.findAll()
            .associateBy({ it.id }, { it.name })

        val images = imageRepository.findAll()
        logger.debug { "🖼️ 전체 이미지 수: ${images.size}" }

        return images.map { image ->
            image.toDto(categories[image.categoryId])
        }
    }

    override fun findById(imageId: Long): Image {
        return imageRepository.findById(imageId)
            .orElseThrow { IllegalArgumentException("이미지 ID에 해당하는 정보가 없습니다: $imageId") }
    }
}
