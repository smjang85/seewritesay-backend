// 파일 위치: org.lena.domain.feedback.client.AiFeedbackClient.kt
package org.lena.domain.feedback.client

interface AiFeedbackClient {
    fun getFeedback(sentence: String, imageDesc: String): String
}
