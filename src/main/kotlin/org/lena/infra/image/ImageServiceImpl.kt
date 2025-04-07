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

    override fun getDescriptionByImageName(imageName: String): String {
        val image = imgInfoRepository.findByName(imageName)
            ?: throw IllegalArgumentException("이미지 Name에 해당하는 설명을 찾을 수 없습니다: $imageName")

        return image.description ?: throw IllegalStateException("이미지 설명이 비어 있습니다: $imageName")
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
