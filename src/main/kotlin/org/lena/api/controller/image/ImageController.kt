package org.lena.api.controller.image
import mu.KotlinLogging
import org.lena.api.dto.image.ImageResponseDto
import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.image.service.ImageService

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/images")
class ImageController(
    private val imageService: ImageService
) {
    private val logger = KotlinLogging.logger {}
    @GetMapping
    fun getAllImages(): List<ImageResponseDto> {
        logger.info("getAllImages called")
        return imageService.findAll()
    }
}
