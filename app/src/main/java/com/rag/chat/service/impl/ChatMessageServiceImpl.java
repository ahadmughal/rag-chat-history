package com.rag.chat.service.impl;

import com.rag.chat.dto.request.SendMessageRequest;
import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatMessageMapper;
import com.rag.chat.repository.ChatMessageRepository;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.ChatMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatSessionRepository sessionRepository;
    @Autowired
    private ChatMessageRepository messageRepository;
    @Autowired
    private ChatMessageMapper mapper;

    @Override
    public SendMessageResponse sendMessage(SendMessageRequest request) {
        log.info("Sending message for session {}", request.getSessionId());

        // Validate session
        ChatSession session = sessionRepository.findById(request.getSessionId())
                .filter(ChatSession::getActive)
                .orElseThrow(() -> {
                    log.warn("Session inactive or not found: {}", request.getSessionId());
                    return new IllegalStateException("Session inactive or expired");
                });

        // Generate response (placeholder for OpenAI/RAG integration)
        String ragResponse = generateRagResponse(request.getMessage());

        // Save the message
        ChatMessage chatMessage = ChatMessage.builder()
                .session(session)
                .sender(request.getMessage())
                .content(request.getMessage())
                .context(ragResponse) // store AI response as context
                .createdAt(LocalDateTime.now())
                .build();

        ChatMessage saved = messageRepository.save(chatMessage);
        log.info("Message saved successfully with ID {}", saved.getId());

        // Map to response DTO
        return mapper.toSendMessageResponse(saved);
    }

    // TODO: Replace with OpenAI API call
    private String generateRagResponse(String userMessage) {
        return "Auto-response to: " + userMessage;
    }
}
