package org.lena.api.common.exception


class UnauthorizedException(
    override val message: String = "인증되지 않은 사용자입니다."
) : RuntimeException(message)