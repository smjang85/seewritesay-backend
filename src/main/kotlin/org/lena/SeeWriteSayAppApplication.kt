package org.lena

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SeeWriteSayAppApplication

fun main(args: Array<String>) {
    runApplication<SeeWriteSayAppApplication>(*args)
}
