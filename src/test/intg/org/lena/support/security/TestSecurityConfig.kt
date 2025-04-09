package org.lena.support.security

import jakarta.servlet.*
import org.springframework.context.annotation.Configuration

import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer

@Configuration
@Profile("test")
class TestSecurityConfig : WebSecurityCustomizer {
    override fun customize(web: WebSecurity) {
        web.ignoring().requestMatchers("/**") // 전체 요청 무시 (간단한 우회)
    }
}