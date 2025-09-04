package com.rag.chat.config;

import com.rag.chat.security.SessionValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final SessionValidationFilter sessionValidationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/sessions/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // open endpoints
                        .requestMatchers("/messages/send/**").authenticated() // only validate sessions for message sending
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
