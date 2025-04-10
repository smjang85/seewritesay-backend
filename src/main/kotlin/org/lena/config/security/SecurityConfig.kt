package org.lena.config.security

import jakarta.servlet.http.HttpServletResponse
import org.lena.config.filter.JwtAuthenticationFilter
import org.lena.config.security.oauth2.OAuth2SuccessHandler
import org.lena.domain.auth.JwtTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import kotlin.jvm.java

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val oauth2SuccessHandler: OAuth2SuccessHandler,
    private val jwtTokenService: JwtTokenService
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/api/v1/auth/**",
                        "/api/v1/images/**",
                        "/images/**"
                    ).permitAll() // ✅ 비로그인 허용 경로

                    .requestMatchers(
                        "/",
                        "/api/v1/ai/feedback/**",
                        "/api/v1/user/**",
                        "/api/v1/history/**"
                    ).authenticated() // ✅ JWT 필요

                    .anyRequest().denyAll()
            }
            .oauth2Login {
                it.successHandler(oauth2SuccessHandler)
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }
            }
            .build()
    }
}
