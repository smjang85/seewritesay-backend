package org.lena.service.img

import mu.KotlinLogging
import org.lena.entity.img.ImgInfo
import org.lena.repository.img.ImgInfoRepository
import org.springframework.stereotype.Service

@Service
class ImgService(
    private val imgInfoRepository: ImgInfoRepository
) {
    private val logger = KotlinLogging.logger {}

    fun getDescriptionByImgId(imgId: String): String {
        val imgInfo = imgInfoRepository.findByImgName(imgId)
            ?: throw IllegalArgumentException("이미지 ID에 해당하는 설명을 찾을 수 없습니다: $imgId")

        return imgInfo.imgDesc
    }

    fun findAll(): List<ImgInfo> {
        val result = imgInfoRepository.findAll()
        logger.info("result : $result")
        return result
    }

    fun findById(id: Long): ImgInfo {
        return imgInfoRepository.findById(id)
            .orElseThrow { IllegalArgumentException("이미지 ID에 해당하는 정보가 없습니다: $id") }
    }
}
