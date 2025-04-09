package org.lena.api.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val message: String,
    val httpStatus: HttpStatus
) {
    // 공통 예외
    INTERNAL_ERROR("COMMON_500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("COMMON_400", "요청이 잘못되었습니다.", HttpStatus.BAD_REQUEST),

    // 인증/인가
    UNAUTHORIZED("AUTH_401", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("AUTH_403", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 유효성 검증
    VALIDATION_ERROR("VALID_400", "요청 필드 유효성 검사 실패", HttpStatus.BAD_REQUEST),

    // 도메인별 예외
    USER_NOT_FOUND("USER_404", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    IMAGE_NOT_FOUND("IMAGE_404", "이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
}
