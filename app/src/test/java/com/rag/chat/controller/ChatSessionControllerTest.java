package com.rag.chat.controller;

import com.rag.chat.dto.request.ChatSessionRequest;
import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.service.ChatSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatSessionControllerTest {

    @InjectMocks
    private ChatSessionController chatSessionController;

    @Mock
    private ChatSessionService chatSessionService;

    @Mock
    private HttpServletRequest httpRequest;

    @Test
    void testCreateSession_Success() {
        ChatSessionRequest request = new ChatSessionRequest();
        request.setSessionName("Test Session");
        ChatSessionResponse response = mock(ChatSessionResponse.class);
        when(response.getSessionId()).thenReturn("12345");
        when(response.getSessionName()).thenReturn("Test Session");
        when(chatSessionService.createSession(request)).thenReturn(response);
        ChatSessionResponse result = chatSessionController.createSession(request, httpRequest);
        assertNotNull(result);
        assertEquals("12345", result.getSessionId());
        assertEquals("Test Session", result.getSessionName());
        verify(chatSessionService, times(1)).createSession(request);
        assertNull(MDC.get("REQUEST_ID_KEY"));
    }

    @Test
    void testCreateSession_Exception() {
        ChatSessionRequest request = new ChatSessionRequest();
        request.setSessionName("Test Session");
        when(chatSessionService.createSession(request)).thenThrow(new RuntimeException("Error creating session"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatSessionController.createSession(request, httpRequest);
        });
        assertEquals("Error creating session", exception.getMessage());
        verify(chatSessionService, times(1)).createSession(request);
        assertNull(MDC.get("REQUEST_ID_KEY"));
    }

    @Test
    void testUpdateSessionName_Success() {
        String sessionId = "12345";
        ChatSessionRequest request = new ChatSessionRequest();
        request.setSessionName("Updated Session Name");
        ChatSessionResponse response = mock(ChatSessionResponse.class);
        when(chatSessionService.updateSessionName(sessionId, request.getSessionName())).thenReturn(response);
        ResponseEntity<ChatSessionResponse> result = chatSessionController.updateSessionName(sessionId, request);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(chatSessionService, times(1)).updateSessionName(sessionId, request.getSessionName());
    }

    @Test
    void testGetAllSessions_Success() {
        List<ChatSessionResponse> sessions = List.of(mock(ChatSessionResponse.class), mock(ChatSessionResponse.class));
        when(chatSessionService.getAllSessions()).thenReturn(sessions);
        List<ChatSessionResponse> result = chatSessionController.getAllSessions();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(chatSessionService, times(1)).getAllSessions();
    }

    @Test
    void testMarkSessionAsFavorite_Success() {
        String sessionId = "12345";
        ChatSessionResponse response = mock(ChatSessionResponse.class);
        when(chatSessionService.markAsFavorite(sessionId)).thenReturn(response);
        ResponseEntity<ChatSessionResponse> result = chatSessionController.markSessionAsFavorite(sessionId);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(chatSessionService, times(1)).markAsFavorite(sessionId);
    }

    @Test
    void testDeleteSession_Success() {
        String sessionId = "12345";
        ResponseEntity<Void> result = chatSessionController.deleteSession(sessionId);
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(chatSessionService, times(1)).deleteSession(sessionId);
    }

    @Test
    void testGetActiveSession_Success() {
        ChatSessionResponse response = mock(ChatSessionResponse.class);
        when(chatSessionService.getActiveSession()).thenReturn(java.util.Optional.of(response));
        ResponseEntity<ChatSessionResponse> result = chatSessionController.getActiveSession();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(chatSessionService, times(1)).getActiveSession();
    }

    @Test
    void testGetActiveSession_NotFound() {
        when(chatSessionService.getActiveSession()).thenReturn(java.util.Optional.empty());
        ResponseEntity<ChatSessionResponse> result = chatSessionController.getActiveSession();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(chatSessionService, times(1)).getActiveSession();
    }
}