package org.lena.api.common.handler

import jakarta.servlet.http.HttpServletRequest
import org.lena.api.common.dto.ApiResponse
import org.lena.api.common.exception.ErrorCode
import org.lena.api.common.exception.UnauthorizedException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        e: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val error = ErrorCode.INTERNAL_ERROR
        return ResponseEntity
            .status(error.httpStatus)
            .body(ApiResponse.error(error, path = request.requestURI))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }
        val error = ErrorCode.VALIDATION_ERROR
        return ResponseEntity
            .status(error.httpStatus)
            .body(ApiResponse.error(error, customMessage = errors, path = request.requestURI))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(
        e: UnauthorizedException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val error = ErrorCode.UNAUTHORIZED
        return ResponseEntity
            .status(error.httpStatus)
            .body(ApiResponse.error(error, path = request.requestURI))
    }
}
