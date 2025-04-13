package org.lena.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    var allowedOrigins: List<String> = listOf()
)
