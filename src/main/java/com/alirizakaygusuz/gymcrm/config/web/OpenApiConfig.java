package com.alirizakaygusuz.gymcrm.config.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Gym CRM API").version("1.0"));
    }

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            if (path.equals("/api/v1/login") ||
                    path.equals("/api/v1/trainees") ||
                    path.equals("/api/v1/trainers")) {
                return;
            }

            pathItem.readOperations().forEach(operation -> {
                operation.addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                        .in("header")
                        .schema(new io.swagger.v3.oas.models.media.StringSchema())
                        .name("X-Username")
                        .description("Username header")
                        .required(true));
                operation.addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                        .in("header")
                        .schema(new io.swagger.v3.oas.models.media.StringSchema())
                        .name("X-Password")
                        .description("Password header")
                        .required(true));
            });
        });

    }
}