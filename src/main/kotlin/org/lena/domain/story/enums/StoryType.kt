package org.lena.domain.story.enums

enum class StoryType(val code: String, val description: String) {
    SHORT("S", "단편"),
    LONG("L", "장편");

    companion object {
        fun fromCode(code: String): StoryType =
            values().find { it.code == code }
                ?: throw IllegalArgumentException("유효하지 않은 스토리 타입: $code")
    }
}