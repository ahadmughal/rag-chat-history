package com.rag.chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RAG Chat API")
                        .version("1.0")
                        .description("API documentation for RAG Chat application")
                        .contact(new Contact()
                                .name("RAG Chat Team")
                                .email("mr.abdulahad1994@gmail.com")
                        )
                );
    }
}
