package com.rag.chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.rag.chat.constants.AppConstants.API_KEY;
import static com.rag.chat.constants.AppConstants.X_API_KEY;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RAG Chat API")
                        .version("1.0")
                        .description("APIs for RAG Chat Case Study"))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(API_KEY, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(X_API_KEY)));
    }
}
