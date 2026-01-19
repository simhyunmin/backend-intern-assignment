package com.simhyeonmin.assignment.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger (OpenAPI 3) 설정을 위한 Configuration 클래스.
 * API 문서의 기본 정보, 보안 설정 (JWT), 그리고 서버 URL을 정의합니다.
 */
@Configuration
class SwaggerConfig {

    /**
     * 커스텀 OpenAPI 빈을 생성합니다.
     * API의 제목, 설명, 버전 및 JWT 인증 방식을 설정합니다.
     *
     * @return [OpenAPI] 객체.
     */
    @Bean
    fun customOpenAPI(): OpenAPI {
        val info = Info()
            .title("Sionic AI Chatbot API")
            .description("Sionic AI 인턴 과제 Chatbot API 명세서")
            .version("1.0.0")

        // JWT Bearer 인증 방식을 정의합니다.
        val securitySchemeName = "bearerAuth"
        val securityRequirement = SecurityRequirement().addList(securitySchemeName)
        val components = Components()
            .addSecuritySchemes(
                securitySchemeName,
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )

        return OpenAPI()
            .addServersItem(Server().url("/"))
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(components)
    }
}
