package com.rag.chat.security;

import com.rag.chat.entity.ChatSession;
import com.rag.chat.repository.ChatSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class SessionValidationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SessionValidationFilter.class);

    private final ChatSessionRepository chatSessionRepository;

    public SessionValidationFilter(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId); // For tracing logs

        String path = request.getRequestURI();

        // Open endpoints
        if (path.contains("/sessions/create") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            MDC.clear();
            return;
        }

        // Validate session headers
        String sessionIdHeader = request.getHeader("X-Session-Id");
        String userIdHeader = request.getHeader("X-User-Id");

        if (sessionIdHeader == null || userIdHeader == null) {
            logger.warn("Missing session headers for request {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing session or user information");
            MDC.clear();
            return;
        }

        Long sessionId;
        Long userId;
        try {
            sessionId = Long.parseLong(sessionIdHeader);
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            logger.warn("Invalid session or user ID headers: {}, {}", sessionIdHeader, userIdHeader);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session or user id");
            MDC.clear();
            return;
        }

        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty() ||
                !sessionOpt.get().getUserId().equals(userId) ||
                sessionOpt.get().getActive() == null ||
                !sessionOpt.get().getActive()) {

            logger.warn("Unauthorized access: sessionId={}, userId={}, path={}", sessionId, userId, path);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid session or unauthorized access");
            MDC.clear();
            return;
        }

        logger.info("Valid session request: sessionId={}, userId={}, path={}", sessionId, userId, path);
        filterChain.doFilter(request, response);
        MDC.clear();
    }
}
