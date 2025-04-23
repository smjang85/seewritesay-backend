package org.lena.domain.feedback.service

import org.lena.api.dto.feedback.reading.AiReadingFeedbackResponseDto
import org.lena.api.dto.feedback.writing.AiWritingFeedbackResponseDto
import org.springframework.web.multipart.MultipartFile

interface AiFeedbackService {
    fun generateWritingFeedback(sentence: String, imageId: Long): AiWritingFeedbackResponseDto
    fun generateReadingFeedback(sentence: String?, userId: Long, imageId: Long, file: MultipartFile): AiReadingFeedbackResponseDto
}