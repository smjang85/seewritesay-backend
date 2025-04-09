package org.lena.api.common.dto

import org.lena.api.common.exception.ErrorCode
import java.time.LocalDateTime

data class ApiResponse<T>(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String? = null,
    val status: Int,
    val message: String,
    val data: T? = null,
    val errorCode: String? = null
) {
    companion object {
        fun <T> success(data: T, message: String = "성공", path: String? = null): ApiResponse<T> =
            ApiResponse(status = 200, message = message, data = data, path = path)

        fun <T> created(data: T, message: String = "생성됨", path: String? = null): ApiResponse<T> =
            ApiResponse(status = 201, message = message, data = data, path = path)

        // 기본 에러 처리 (기존)
        fun <T> error(
            errorCode: ErrorCode,
            path: String? = null
        ): ApiResponse<T> {
            return ApiResponse(
                status = errorCode.httpStatus.value(),
                message = errorCode.message,
                errorCode = errorCode.code,
                path = path
            )
        }

        fun <T> error(
            message: String,
            status: Int = 400,
            errorCode: String? = null,
            path: String? = null
        ): ApiResponse<T> =
            ApiResponse(status = status, message = message, errorCode = errorCode, path = path)

        fun <T> error(
            errorCode: ErrorCode,
            customMessage: String,
            path: String? = null
        ): ApiResponse<T> {
            return ApiResponse(
                status = errorCode.httpStatus.value(),
                message = customMessage,
                errorCode = errorCode.code,
                path = path
            )
        }
    }
}
