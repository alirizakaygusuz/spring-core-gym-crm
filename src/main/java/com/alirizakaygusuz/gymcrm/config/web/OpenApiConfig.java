package com.alirizakaygusuz.gymcrm.config.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym CRM API")
                        .version("1.0"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer securityCustomizer() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {

            boolean isPublic = path.equals("/api/v1/login") ||
                    path.equals("/api/v1/trainees") ||
                    path.equals("/api/v1/trainers") ||
                    path.equals("/api/v1/trainings/types");

            if (isPublic) {
                return;
            }

            pathItem.readOperations().forEach(operation ->
                    operation.addSecurityItem(new SecurityRequirement()
                            .addList(SECURITY_SCHEME_NAME))
            );
        });
    }
}