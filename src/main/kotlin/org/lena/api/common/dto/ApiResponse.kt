package org.lena.api.common.dto

import org.lena.api.common.exception.ErrorCode
import java.time.LocalDateTime

data class ApiResponse<T>(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String? = null,
    val status: Int,
    val message: String,
    val data: T? = null,
    val errorCode: String? = null,
) {
    companion object {

        // ✅ 성공 응답 - data nullable 허용
        fun <T> success(
            data: T? = null,
            message: String = "성공",
            path: String? = null
        ): ApiResponse<T> = ApiResponse(
            status = 200,
            message = message,
            data = data,
            path = path
        )

        // ✅ 생성 응답 - data nullable 허용
        fun <T> created(
            data: T? = null,
            message: String = "생성됨",
            path: String? = null
        ): ApiResponse<T> = ApiResponse(
            status = 201,
            message = message,
            data = data,
            path = path
        )

        // ✅ 에러 - ErrorCode 기반
        fun <T> error(
            errorCode: ErrorCode,
            path: String? = null
        ): ApiResponse<T> = ApiResponse(
            status = errorCode.httpStatus.value(),
            message = errorCode.message,
            errorCode = errorCode.code,
            path = path
        )

        // ✅ 에러 - ErrorCode + 커스텀 메시지
        fun <T> error(
            errorCode: ErrorCode,
            customMessage: String,
            path: String? = null
        ): ApiResponse<T> = ApiResponse(
            status = errorCode.httpStatus.value(),
            message = customMessage,
            errorCode = errorCode.code,
            path = path
        )

        // ✅ 에러 - 직접 status, message 지정
        fun <T> error(
            message: String,
            status: Int = 400,
            errorCode: String? = null,
            path: String? = null
        ): ApiResponse<T> = ApiResponse(
            status = status,
            message = message,
            errorCode = errorCode,
            path = path
        )
    }
}
