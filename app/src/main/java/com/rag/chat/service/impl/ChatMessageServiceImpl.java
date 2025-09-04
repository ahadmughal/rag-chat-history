package com.rag.chat.service.impl;

import com.rag.chat.dto.request.SendMessageRequest;
import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatMessageMapper;
import com.rag.chat.repository.ChatMessageRepository;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.ChatMessageService;
import com.rag.chat.service.OpenAiRagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private OpenAiRagService openAiRagService;

    @Override
    public SendMessageResponse sendMessage(SendMessageRequest request) {
        log.info("Sending message for session {}", request.getSessionId());

        // Validate session exists (ignore active/inactive status)
        ChatSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> {
                    log.warn("Session not found: {}", request.getSessionId());
                    return new IllegalStateException("Session not found");
                });

        // Save user message
        ChatMessage chatMessage = ChatMessage.builder()
                .session(session)
                .sender("user")
                .content(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
        ChatMessage savedMessage = messageRepository.save(chatMessage);
        log.info("User message saved successfully with ID {}", savedMessage.getId());

        // Generate AI response
        String botResponse;
        try {
            botResponse = openAiRagService.generateResponse(request.getMessage());
        } catch (Exception e) {
            log.error("Error while generating AI response: {}", e.getMessage(), e);
            botResponse = "Sorry, I couldn't process your message at the moment. Please try again later.";
        }

        // Update the same message row with bot response
        savedMessage.setContext(botResponse);
        ChatMessage updatedMessage = messageRepository.save(savedMessage);
        log.info("Bot response updated for message ID {}", updatedMessage.getId());

        // Map the updated message to response DTO
        return mapper.toSendMessageResponse(updatedMessage);
    }

    @Override
    public List<SendMessageResponse> getMessagesBySession(String sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not found"));

        List<ChatMessage> messages = messageRepository.findBySessionOrderByCreatedAtAsc(session);

        return messages.stream()
                .map(mapper::toSendMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> search(String query, String sessionId) {
        if (sessionId != null) {
            // Return all messages of the session
            return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        } else {
            // Search messages across all sessions
            return messageRepository.findByContentContainingIgnoreCaseOrderByCreatedAtAsc(query);
        }
    }
}
