package com.bilkent.devinsight.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@OpenAPIDefinition
@Configuration
@SecurityScheme(
        name = "token",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"

)
public class SpringdocConfig {
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI().info(new Info().title("DevInsight").version("1.0.0.").description("DevInsight Swagger Documentation"));
    }



}
