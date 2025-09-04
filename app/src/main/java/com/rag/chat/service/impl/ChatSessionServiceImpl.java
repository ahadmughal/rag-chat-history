package com.rag.chat.service.impl;

import com.rag.chat.dto.request.CreateSessionRequest;
import com.rag.chat.dto.response.CreateSessionResponse;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatSessionMapper;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl implements ChatSessionService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;
    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Override
    public CreateSessionResponse createSession(CreateSessionRequest request) {
        ChatSession session = ChatSession.builder()
                .userId(request.getUserId())
                .sessionName(request.getSessionName() != null ? request.getSessionName() : "New Chat")
                .isFavorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ChatSession saved = chatSessionRepository.save(session);

        // Delegate mapping to mapper
        return chatSessionMapper.toCreateSessionResponse(saved);
    }

    @Override
    public void deactivateOldSessions(Long userId) {
        List<ChatSession> activeSessions = chatSessionRepository.findByUserId(userId);
        activeSessions.forEach(session -> {
            session.setActive(false);
            chatSessionRepository.save(session);
        });
    }
}
