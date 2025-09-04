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

        String path = request.getRequestURI();

        // Only validate session for message sending endpoints
        if (path.startsWith("/messages/send")) {
            String sessionIdHeader = request.getHeader("X-Session-Id");
            if (sessionIdHeader == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing session id");
                return;
            }

            Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionIdHeader);
            if (sessionOpt.isEmpty() || !Boolean.TRUE.equals(sessionOpt.get().getActive())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Session inactive or expired");
                return;
            }
        }

        filterChain.doFilter(request, response); // allow all other requests
    }
}
