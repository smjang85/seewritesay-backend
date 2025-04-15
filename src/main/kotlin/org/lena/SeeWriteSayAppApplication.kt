package org.lena

import org.lena.config.properties.security.CorsProperties
import org.lena.config.properties.user.NicknameProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
    value = [CorsProperties::class, NicknameProperties::class]
)
class SeeWriteSayAppApplication

fun main(args: Array<String>) {
    runApplication<SeeWriteSayAppApplication>(*args)
}
