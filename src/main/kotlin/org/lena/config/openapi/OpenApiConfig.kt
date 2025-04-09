package org.lena.config.openapi

import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("SeeWriteSay API")
                    .description("AI 기반 영어 작문 학습 서비스 API 명세서")
                    .version("v1.0.0")
            )
    }
}