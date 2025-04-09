package org.lena.support.test

import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

inline fun <reified T> WebTestClient.ResponseSpec.expectApiResponse(): T {
    return this.expectBody(object : ParameterizedTypeReference<T>() {}).returnResult().responseBody!!
}