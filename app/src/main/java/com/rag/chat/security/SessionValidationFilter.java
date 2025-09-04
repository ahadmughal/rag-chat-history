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

        // Validate session header
        String sessionId = request.getHeader("X-Session-Id");

        if (sessionId == null) {
            logger.warn("Missing session header for request {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing session information");
            MDC.clear();
            return;
        }

        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty() || sessionOpt.get().getActive() == null || !sessionOpt.get().getActive()) {
            logger.warn("Unauthorized access: sessionId={}, path={}", sessionId, path);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid session or inactive");
            MDC.clear();
            return;
        }

        logger.info("Valid session request: sessionId={}, path={}", sessionId, path);
        filterChain.doFilter(request, response);
        MDC.clear();
    }
}
