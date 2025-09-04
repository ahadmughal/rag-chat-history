package com.rag.chat.service.impl;

import com.rag.chat.dto.request.ChatSessionRequest;
import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatSessionMapper;
import com.rag.chat.repository.ChatMessageRepository;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

    private static final Logger logger = LoggerFactory.getLogger(ChatSessionServiceImpl.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Override
    public ChatSessionResponse createSession(ChatSessionRequest request) {
        if (request.getSessionName() == null || request.getSessionName().isBlank()) {
            throw new IllegalArgumentException("Session name must not be empty");
        }

        deactivateOldSessions();

        String sessionId = UUID.randomUUID().toString();

        ChatSession session = ChatSession.builder()
                .id(sessionId)
                .sessionName(request.getSessionName())
                .active(true)
                .favorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ChatSession saved = chatSessionRepository.save(session);
        logger.info("Created new chat session: sessionId={}, sessionName={}", sessionId, request.getSessionName());

        return chatSessionMapper.toCreateSessionResponse(saved);
    }

    @Override
    public List<ChatSessionResponse> getAllSessions() {
        List<ChatSession> sessions = chatSessionRepository.findAllByOrderByCreatedAtDesc();
        return sessions.stream()
                .map(chatSessionMapper::toCreateSessionResponse)
                .toList();
    }

    private void deactivateOldSessions() {
        List<ChatSession> activeSessions = chatSessionRepository.findByActiveTrue();
        for (ChatSession session : activeSessions) {
            session.setActive(false);
            chatSessionRepository.save(session);
            logger.info("Deactivated old session: sessionId={}", session.getId());
        }
    }
    @Override
    public ChatSessionResponse markAsFavorite(String sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not found"));

        session.setFavorite(!session.isFavorite());
        ChatSession updatedSession = chatSessionRepository.save(session);

        return chatSessionMapper.toResponse(updatedSession);
    }

    @Override
    @Transactional
    public void deleteSession(String sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not found"));

        chatMessageRepository.deleteAllBySession(session);

        chatSessionRepository.delete(session);

        log.info("Deleted session {} and all related messages", sessionId);
    }

    @Override
    public Optional<ChatSessionResponse> getActiveSession() {
        List<ChatSession> activeSessions = chatSessionRepository.findByActiveTrue();
        if (activeSessions.isEmpty()) {
            return Optional.empty();
        }

        ChatSession session = activeSessions.get(0);
        ChatSessionResponse response = ChatSessionResponse.builder()
                .sessionId(session.getId())
                .sessionName(session.getSessionName())
                .active(session.getActive())
                .favorite(session.isFavorite())
                .build();

        return Optional.of(response);
    }
}
