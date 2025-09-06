package com.rag.chat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.rag.chat.constants.AppConstants.*;

@Slf4j
@Component
@Order(1)
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String apiKey;

    public ApiKeyAuthFilter(@Value("${app.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info(CHECKING_PATH_FILTER, path);

        // Skip Swagger + OPTIONS
        boolean shouldSkip =
                "OPTIONS".equalsIgnoreCase(request.getMethod()) ||
                        path.startsWith("/swagger-ui") ||
                        path.startsWith("/v3/api-docs") ||
                        path.startsWith("/webjars") ||
                        path.startsWith("/swagger-resources") ||
                        path.equals("/swagger-ui.html") ||
                        path.startsWith("/rag-chat/swagger-ui") ||
                        path.startsWith("/rag-chat/v3/api-docs") ||
                        path.startsWith("/rag-chat/webjars") ||
                        path.startsWith("/rag-chat/swagger-resources") ||
                        path.equals("/rag-chat/swagger-ui.html");

        log.info(KEEP_FILTERING_PATH, path, shouldSkip);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info(API_KEY_FILTER_PROCESSING, path);

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerApiKey = request.getHeader(X_API_KEY);
        if (headerApiKey == null || !headerApiKey.equals(apiKey)) {
            log.warn(API_KEY_VALIDATION_FAILED, path);
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException(INVALID_KEY);
        }

        log.info(API_KEY_VALIDATION_SUCCESS, path);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "apiKeyUser",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_API"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
