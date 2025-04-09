package org.lena.config.resolver

import org.lena.api.common.annotation.CurrentUser
import org.lena.api.common.exception.UnauthorizedException
import org.lena.config.security.CustomUserPrincipal
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.stereotype.Component

@Component
class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(CurrentUser::class.java) != null &&
                parameter.parameterType == CustomUserPrincipal::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.principal

        return (principal as? CustomUserPrincipal)
            ?: throw UnauthorizedException("로그인이 필요합니다.")
    }
}
