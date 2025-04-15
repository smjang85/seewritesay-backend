package org.lena.config.properties.user

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "nickname")
data class NicknameProperties(
    val korean: WordGroup,
    val english: WordGroup
) {
    data class WordGroup(
        val adjectives: List<String>,
        val animals: List<String>
    )
}
