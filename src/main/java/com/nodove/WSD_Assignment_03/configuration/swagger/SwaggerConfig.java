package com.nodove.WSD_Assignment_03.configuration.swagger;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Bearer Token 인증
        final String bearerAuthScheme = "bearerAuth";
        SecurityScheme bearerScheme = new SecurityScheme()
                .name(bearerAuthScheme)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Cookie 인증
        final String cookieAuthScheme = "cookieAuth";
        SecurityScheme cookieScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("refreshToken"); // 쿠키 이름과 일치해야 함

        // Security Scheme 설정
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(cookieAuthScheme)
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(bearerAuthScheme, bearerScheme) // Bearer 인증 스키마 추가
                        .addSecuritySchemes(cookieAuthScheme, cookieScheme)) // Cookie 인증 스키마 추가
                .info(new Info()
                        .title("API Documentation")
                        .version("1.0")
                        .description("API documentation for the application."));
    }
}
