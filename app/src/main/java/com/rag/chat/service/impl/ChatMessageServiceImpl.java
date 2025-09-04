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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.rag.chat.constants.AppConstants.*;

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
        log.info(SENDING_MESSAGE_FOR_SESSION, request.getSessionId());
        ChatSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> {
                    log.warn(SESSION_NOT_FOUND_LOG, request.getSessionId());
                    return new IllegalStateException(SESSION_NOT_FOUND_EXEC);
                });
        ChatMessage savedMessage = buildChatMessageAndSave(session, request.getMessage());
        log.info(USER_MESSAGE_SAVED_LOG, savedMessage.getId());
        String botResponse;
        try {
            botResponse = openAiRagService.generateResponse(request.getMessage());
        } catch (Exception e) {
            log.error(ERROR_AI_RESPONSE_LOG, e.getMessage(), e);
            botResponse = BOT_RESPONSE_EXEC;
        }
        savedMessage.setContext(botResponse);
        ChatMessage updatedMessage = messageRepository.save(savedMessage);
        log.info(BOT_RESPONSE_UPDATED, updatedMessage.getId());
        return mapper.toSendMessageResponse(updatedMessage);
    }

    private ChatMessage buildChatMessageAndSave(ChatSession session, String message){
        ChatMessage chatMessage = ChatMessage.builder()
                .session(session)
                .sender(SENDER_USER)
                .content(message)
                .createdAt(LocalDateTime.now())
                .build();
        return messageRepository.save(chatMessage);
    }

    @Override
    public List<SendMessageResponse> getMessagesBySession(String sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException(SESSION_NOT_FOUND_EXEC));
        List<ChatMessage> messages = messageRepository.findBySessionOrderByCreatedAtAsc(session);
        return messages.stream()
                .map(mapper::toSendMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> search(String query, String sessionId) {
        return StringUtils.isNotBlank(sessionId)  ? messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId) : messageRepository.findByContentContainingIgnoreCaseOrderByCreatedAtAsc(query);
    }
}
