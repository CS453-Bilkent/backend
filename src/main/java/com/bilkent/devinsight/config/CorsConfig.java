package com.bilkent.devinsight.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] CORS_ALLOWED_ORIGINS = {
            "http://localhost:5173",
            "https://localhost:5173",
            "http://devinsight.xyz",
            "https://devinsight.xyz",
            "http://www.devinsight.xyz",
            "https://www.devinsight.xyz"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(CORS_ALLOWED_ORIGINS) // TODO: get this from environment
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
