package org.lena.config.security

import jakarta.servlet.http.HttpServletResponse
import org.lena.config.filter.JwtAuthenticationFilter
import org.lena.config.properties.security.CorsProperties
import org.lena.config.security.oauth2.OAuth2SuccessHandler
import org.lena.domain.auth.JwtTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@Profile("!test")
class SecurityConfig(
    private val oauth2SuccessHandler: OAuth2SuccessHandler,
    private val jwtTokenService: JwtTokenService,
    private val corsProperties: CorsProperties
) {

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val corsConfiguration = CorsConfiguration().apply {
            allowedOrigins = corsProperties.allowedOrigins
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowedHeaders = listOf("Authorization", "Content-Type")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfiguration)
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .headers {
                it
                    .xssProtection { xss ->
                        xss.headerValue(HeaderValue.ENABLED_MODE_BLOCK)
                    }
                    .frameOptions { frame -> frame.sameOrigin() }
                    .httpStrictTransportSecurity { hsts ->
                        hsts.includeSubDomains(true).maxAgeInSeconds(31536000)
                    }
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives(
                            "default-src 'self'; " +
                                    "script-src 'self'; " +
                                    "style-src 'self' 'unsafe-inline'; " +
                                    "img-src 'self' data:; " +
                                    "connect-src 'self'; " +
                                    "font-src 'self'; " +
                                    "frame-ancestors 'none';"
                        )
                    }
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/api/v1/auth/**",
                        "/api/v1/images/**",
                        "/images/**"
                    ).permitAll()
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll() // 헬스, 인포는 오픈
                    .requestMatchers(
                        "/",
                        "/api/v1/ai/feedback/**",
                        "/api/v1/user/**",
                        "/api/v1/history/**"
                    ).authenticated()
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
