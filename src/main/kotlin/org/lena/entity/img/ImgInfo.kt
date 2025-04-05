package org.lena.entity.img

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "IMG_INFO")
data class ImgInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "img_id")
    val imgId: String = "",

    @Column(name = "img_name")
    val imgName: String = "",

    @Column(name = "img_path")
    val imgPath: String = "",

    @Column(name = "img_desc")
    val imgDesc: String = ""
) {
    constructor() : this(0, "", "", "") // ✅ 기본 생성자 추가
}
