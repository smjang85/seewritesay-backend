package org.lena.api.common.handler

import jakarta.servlet.http.HttpServletRequest
import org.lena.api.common.dto.ApiResponse
import org.lena.api.common.exception.ErrorCode
import org.lena.api.common.exception.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        e: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.error("❌ RuntimeException 발생: ${e.message}", e)
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR, request.requestURI)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }
        log.warn("⚠️ Validation 실패: $errors")
        return buildErrorResponse(ErrorCode.VALIDATION_ERROR, request.requestURI, customMessage = errors)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(
        e: UnauthorizedException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("🔐 인증 실패: ${e.message}")
        return buildErrorResponse(ErrorCode.UNAUTHORIZED, request.requestURI)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        e: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.error("🚫 잘못된 인자: ${e.message}", e)
        return buildErrorResponse(ErrorCode.VALIDATION_ERROR, request.requestURI, customMessage = e.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(
        e: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        log.error("❗️예기치 못한 예외: ${e.message}", e)
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR, request.requestURI)
    }

    private fun buildErrorResponse(
        errorCode: ErrorCode,
        path: String,
        customMessage: String? = null
    ): ResponseEntity<ApiResponse<Nothing>> {
        val safeMessage = customMessage ?: errorCode.message
        return ResponseEntity
            .status(errorCode.httpStatus)
            .body(ApiResponse.error(errorCode, safeMessage, path))
    }

}
