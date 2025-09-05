package com.rag.chat.service;

import com.rag.chat.dto.request.ChatSessionRequest;
import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatSessionMapper;
import com.rag.chat.repository.ChatMessageRepository;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.impl.ChatSessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatSessionServiceImplTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatSessionMapper chatSessionMapper;

    @InjectMocks
    private ChatSessionServiceImpl chatSessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSession() {
        ChatSessionRequest request = new ChatSessionRequest();
        request.setSessionName("Test Session");
        ChatSession chatSession = ChatSession.builder()
                .id("123")
                .sessionName("Test Session")
                .active(true)
                .favorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ChatSessionResponse response = new ChatSessionResponse();
        when(chatSessionRepository.findByActiveTrue()).thenReturn(Collections.emptyList());
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(response);
        ChatSessionResponse result = chatSessionService.createSession(request);
        assertNotNull(result);
        verify(chatSessionRepository, times(1)).findByActiveTrue();
        verify(chatSessionRepository, times(1)).save(any(ChatSession.class));
        verify(chatSessionMapper, times(1)).toResponse(chatSession);
    }

    @Test
    void testGetAllSessions() {
        ChatSession chatSession = ChatSession.builder()
                .id("123")
                .sessionName("Test Session")
                .build();
        when(chatSessionRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(chatSession));
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(new ChatSessionResponse());
        List<ChatSessionResponse> result = chatSessionService.getAllSessions();
        assertEquals(1, result.size());
        verify(chatSessionRepository, times(1)).findAllByOrderByCreatedAtDesc();
        verify(chatSessionMapper, times(1)).toResponse(chatSession);
    }

    @Test
    void testMarkAsFavorite() {
        ChatSession chatSession = ChatSession.builder()
                .id("123")
                .favorite(false)
                .build();
        when(chatSessionRepository.findById("123")).thenReturn(Optional.of(chatSession));
        when(chatSessionRepository.save(chatSession)).thenReturn(chatSession);
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(new ChatSessionResponse());
        ChatSessionResponse result = chatSessionService.markAsFavorite("123");
        assertNotNull(result);
        assertTrue(chatSession.isFavorite());
        verify(chatSessionRepository, times(1)).findById("123");
        verify(chatSessionRepository, times(1)).save(chatSession);
        verify(chatSessionMapper, times(1)).toResponse(chatSession);
    }

    @Test
    void testDeleteSession() {
        ChatSession chatSession = ChatSession.builder()
                .id("123")
                .build();
        when(chatSessionRepository.findById("123")).thenReturn(Optional.of(chatSession));
        chatSessionService.deleteSession("123");
        verify(chatMessageRepository, times(1)).deleteAllBySession(chatSession);
        verify(chatSessionRepository, times(1)).delete(chatSession);
    }

    @Test
    void testGetActiveSession() {
        ChatSession chatSession = ChatSession.builder()
                .id("123")
                .active(true)
                .build();
        when(chatSessionRepository.findByActiveTrue()).thenReturn(List.of(chatSession));
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(new ChatSessionResponse());
        Optional<ChatSessionResponse> result = chatSessionService.getActiveSession();
        assertTrue(result.isPresent());
        verify(chatSessionRepository, times(1)).findByActiveTrue();
        verify(chatSessionMapper, times(1)).toResponse(chatSession);
    }

    @Test
    void testUpdateSessionName() {
        ChatSession chatSession = ChatSession.builder()
                .id("123")
                .sessionName("Old Name")
                .build();
        when(chatSessionRepository.findById("123")).thenReturn(Optional.of(chatSession));
        when(chatSessionRepository.save(chatSession)).thenReturn(chatSession);
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(new ChatSessionResponse());
        ChatSessionResponse result = chatSessionService.updateSessionName("123", "New Name");
        assertNotNull(result);
        assertEquals("New Name", chatSession.getSessionName());
        verify(chatSessionRepository, times(1)).findById("123");
        verify(chatSessionRepository, times(1)).save(chatSession);
        verify(chatSessionMapper, times(1)).toResponse(chatSession);
    }
}