package org.lena.support.security

import jakarta.servlet.*
import org.lena.config.security.CustomUserPrincipal
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Profile("test")
@Component
class MockJwtAuthenticationFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val mockUser = CustomUserPrincipal(
            id = 1L,
            email = "test@example.com",
            name = "테스트유저"
        )

        val auth = UsernamePasswordAuthenticationToken(mockUser, null, emptyList())
        SecurityContextHolder.getContext().authentication = auth

        chain.doFilter(request, response)
    }
}