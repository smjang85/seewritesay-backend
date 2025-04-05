package org.lena.repository.img

import org.lena.entity.img.ImgInfo
import org.springframework.data.jpa.repository.JpaRepository

interface ImgInfoRepository : JpaRepository<ImgInfo, Long> {
    fun findByImgName(imgName: String): ImgInfo?
}