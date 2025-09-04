package com.rag.chat.service.impl;

import com.rag.chat.dto.request.CreateSessionRequest;
import com.rag.chat.dto.response.CreateSessionResponse;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatSessionMapper;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService {

    private static final Logger logger = LoggerFactory.getLogger(ChatSessionServiceImpl.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;
    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Override
    public CreateSessionResponse createSession(CreateSessionRequest request) {
        if (request.getSessionName() == null || request.getSessionName().isBlank()) {
            throw new IllegalArgumentException("Session name must not be empty");
        }

        // Deactivate any old active sessions before creating new one
        deactivateOldSessions();

        // Generate unique session ID
        String sessionId = UUID.randomUUID().toString();

        ChatSession session = ChatSession.builder()
                .id(sessionId)
                .sessionName(request.getSessionName())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ChatSession saved = chatSessionRepository.save(session);
        logger.info("Created new chat session: sessionId={}, sessionName={}", sessionId, request.getSessionName());

        return chatSessionMapper.toCreateSessionResponse(saved);
    }

    private void deactivateOldSessions() {
        List<ChatSession> activeSessions = chatSessionRepository.findByActiveTrue();
        for (ChatSession session : activeSessions) {
            session.setActive(false);
            chatSessionRepository.save(session);
            logger.info("Deactivated old session: sessionId={}", session.getId());
        }
    }
}
