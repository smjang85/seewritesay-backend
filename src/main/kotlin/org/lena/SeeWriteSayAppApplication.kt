package org.lena

import org.lena.config.properties.CorsProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableConfigurationProperties(CorsProperties::class)
@ComponentScan(basePackages = ["org.lena"])
class SeeWriteSayAppApplication

fun main(args: Array<String>) {
    runApplication<SeeWriteSayAppApplication>(*args)
}
