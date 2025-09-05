package com.rag.chat.controller;

import com.rag.chat.dto.request.SendMessageRequest;
import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import com.rag.chat.service.ChatMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageControllerTest {

    @InjectMocks
    private ChatMessageController chatMessageController;

    @Mock
    private ChatMessageService messageService;

    @Test
    void testSendMessage_Success() {
        SendMessageRequest request = new SendMessageRequest();
        request.setSessionId("12345");
        SendMessageResponse response = mock(SendMessageResponse.class);
        when(messageService.sendMessage(request)).thenReturn(response);
        ResponseEntity<SendMessageResponse> result = chatMessageController.sendMessage(request);
        assertNotNull(result);
        assertEquals(response, result.getBody());
        verify(messageService, times(1)).sendMessage(request);
    }

    @Test
    void testGetMessages_Success() {
        String sessionId = "12345";
        List<SendMessageResponse> messages = List.of(mock(SendMessageResponse.class), mock(SendMessageResponse.class));
        when(messageService.getMessagesBySession(sessionId)).thenReturn(messages);
        ResponseEntity<List<SendMessageResponse>> result = chatMessageController.getMessages(sessionId);
        assertNotNull(result);
        assertEquals(messages, result.getBody());
        verify(messageService, times(1)).getMessagesBySession(sessionId);
    }

    @Test
    void testSearchMessages_Success() {
        String query = "test";
        String sessionId = "12345";
        List<ChatMessage> results = List.of(mock(ChatMessage.class), mock(ChatMessage.class));
        when(messageService.search(query, sessionId)).thenReturn(results);
        ResponseEntity<?> result = chatMessageController.searchMessages(query, sessionId);
        assertNotNull(result);
        assertEquals(results, result.getBody());
        verify(messageService, times(1)).search(query, sessionId);
    }

    @Test
    void testSearchMessages_NoQuery() {
        String sessionId = "12345";
        List<ChatMessage> results = List.of(mock(ChatMessage.class));
        when(messageService.search(null, sessionId)).thenReturn(results);
        ResponseEntity<?> result = chatMessageController.searchMessages(null, sessionId);
        assertNotNull(result);
        assertEquals(results, result.getBody());
        verify(messageService, times(1)).search(null, sessionId);
    }
}