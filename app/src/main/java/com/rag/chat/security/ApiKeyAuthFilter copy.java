package com.rag.chat.security;

import com.rag.chat.config.ApiKeyConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.rag.chat.constants.AppConstants.*;

@Slf4j
@Component
@Order(1)
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Autowired
    private ApiKeyConfig apiKeyConfig;

    private List<String> apiKeys;

    private static final int MAX_REQUESTS = 60;
    private static final long TIME_WINDOW_MS = 60 * 1000; // 1 minute

    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Initialize apiKeys once
        if (apiKeys == null) {
            apiKeys = apiKeyConfig.getKeys();
            for (String key : apiKeys) {
                rateLimitMap.putIfAbsent(key, new RateLimitInfo(0, Instant.now().toEpochMilli()));
            }
        }

        String path = request.getRequestURI();
        String headerApiKey = request.getHeader(X_API_KEY);

        log.info(API_KEY_FILTER_PROCESSING, path);

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check API key presence
        if (headerApiKey == null) {
            log.warn(API_KEY_VALIDATION_FAILED, path);
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing API Key");
            return;
        }

        // Check API key validity
        if (!apiKeys.contains(headerApiKey)) {
            log.warn(API_KEY_VALIDATION_FAILED, path);
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }

        // Check rate limit for this key
        RateLimitInfo info = rateLimitMap.get(headerApiKey);
        long now = Instant.now().toEpochMilli();
        synchronized (info) {
            if (now - info.lastResetTime >= TIME_WINDOW_MS) {
                info.requestCount = 0;
                info.lastResetTime = now;
            }
            if (info.requestCount >= MAX_REQUESTS) {
                log.warn(RATE_LIMIT_LOG, headerApiKey);
                sendJsonError(response, 429, RATE_LIMIT_EXEC);
                return;
            }
            info.requestCount++;
        }

        // Set authentication
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "apiKeyUser",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_API"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType(CONTENT_TYPE_JSON);
        response.setStatus(status);
        String json = String.format(
                STRING_FORMAT,
                Instant.now(), status, message
        );
        response.getWriter().write(json);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = "OPTIONS".equalsIgnoreCase(request.getMethod()) ||
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

    private static class RateLimitInfo {
        private int requestCount;
        private long lastResetTime;

        public RateLimitInfo(int requestCount, long lastResetTime) {
            this.requestCount = requestCount;
            this.lastResetTime = lastResetTime;
        }
    }
}
