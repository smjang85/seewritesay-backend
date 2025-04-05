package org.lena.controller.img
import mu.KotlinLogging
import org.lena.entity.img.ImgInfo
import org.lena.service.img.ImgService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/images")
class ImgController(
    private val imgService: ImgService
) {
    private val logger = KotlinLogging.logger {}
    @GetMapping
    fun getAllImages(): List<ImgInfo> {
        logger.info("getAllImages called")
        return imgService.findAll()
    }
}
