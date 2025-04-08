package org.lena.infra.image

import mu.KotlinLogging
import org.lena.api.dto.image.ImageResponseDto
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.image.entity.Image

import org.lena.domain.image.service.ImageService
import org.springframework.stereotype.Service

@Service
class ImageServiceImpl(
    private val imgInfoRepository: ImageRepository
) : ImageService {

    private val logger = KotlinLogging.logger {}

    override fun getDescriptionByImageId(imageId: Long): String {
        val image: Image = imgInfoRepository.findById(imageId)
            .orElseThrow { IllegalStateException("존재하지 않는 이미지 ID입니다: $imageId") }

        return image.description ?: throw IllegalStateException("이미지 설명이 비어 있습니다: $imageId")
    }

    override fun findAll(): List<ImageResponseDto> {
        val result = imgInfoRepository.findAll()
        logger.info { "image find All : $result" }

        return result.map {
            ImageResponseDto(
                id = it.id,
                name = it.name,
                path = it.path,
                category = it.category,
                description = it.description
            )
        }
    }

    override fun findById(id: Long): Image {
        return imgInfoRepository.findById(id)
            .orElseThrow { IllegalArgumentException("이미지 ID에 해당하는 정보가 없습니다: $id") }
    }
}
