package org.lena.domain.story.enums;

enum class LanguageCode(val code: String) {
    KO("ko"),
    EN("en"),
    KO_EN("ko_en"),
    EN_KO("en_ko");

    companion object {
        fun from(code: String): LanguageCode {
            return entries.firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Unsupported language code: $code")
        }

        fun allCodes(): List<String> = values().map { it.code }
    }

}
