package com.rag.chat.service;

import com.rag.chat.dto.request.SendMessageRequest;
import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatMessageMapper;
import com.rag.chat.repository.ChatMessageRepository;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatMessageServiceImplTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private ChatMessageMapper mapper;

    @Mock
    private OpenAiRagService openAiRagService;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessage() {
        SendMessageRequest request = new SendMessageRequest();
        request.setSessionId("123");
        request.setMessage("Hello");
        ChatSession session = ChatSession.builder().id("123").build();
        ChatMessage savedMessage = ChatMessage.builder()
                .id(456L)
                .session(session)
                .content("Hello")
                .createdAt(LocalDateTime.now())
                .build();
        when(sessionRepository.findById("123")).thenReturn(Optional.of(session));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(savedMessage);
        when(openAiRagService.generateResponse("Hello")).thenReturn("Hi there!");
        when(mapper.toSendMessageResponse(any(ChatMessage.class))).thenReturn(new SendMessageResponse());
        SendMessageResponse response = chatMessageService.sendMessage(request);
        assertNotNull(response);
        verify(sessionRepository, times(1)).findById("123");
        verify(messageRepository, times(2)).save(any(ChatMessage.class));
        verify(openAiRagService, times(1)).generateResponse("Hello");
        verify(mapper, times(1)).toSendMessageResponse(any(ChatMessage.class));
    }

    @Test
    void testGetMessagesBySession() {
        ChatSession session = ChatSession.builder().id("123").build();
        ChatMessage message = ChatMessage.builder()
                .id(456L)
                .session(session)
                .content("Hello")
                .createdAt(LocalDateTime.now())
                .build();
        when(sessionRepository.findById("123")).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(List.of(message));
        when(mapper.toSendMessageResponse(message)).thenReturn(new SendMessageResponse());
        List<SendMessageResponse> responses = chatMessageService.getMessagesBySession("123");
        assertEquals(1, responses.size());
        verify(sessionRepository, times(1)).findById("123");
        verify(messageRepository, times(1)).findBySessionOrderByCreatedAtAsc(session);
        verify(mapper, times(1)).toSendMessageResponse(message);
    }

    @Test
    void testSearchWithSessionId() {
        ChatMessage message = ChatMessage.builder()
                .id(456L)
                .content("Hello")
                .createdAt(LocalDateTime.now())
                .build();
        when(messageRepository.findBySessionIdOrderByCreatedAtAsc("123")).thenReturn(List.of(message));
        List<ChatMessage> results = chatMessageService.search(null, "123");
        assertEquals(1, results.size());
        verify(messageRepository, times(1)).findBySessionIdOrderByCreatedAtAsc("123");
    }

    @Test
    void testSearchWithQuery() {
        ChatMessage message = ChatMessage.builder()
                .id(456L)
                .content("Hello")
                .createdAt(LocalDateTime.now())
                .build();
        when(messageRepository.findByContentContainingIgnoreCaseOrderByCreatedAtAsc("Hello")).thenReturn(List.of(message));
        List<ChatMessage> results = chatMessageService.search("Hello", null);
        assertEquals(1, results.size());
        verify(messageRepository, times(1)).findByContentContainingIgnoreCaseOrderByCreatedAtAsc("Hello");
    }
}