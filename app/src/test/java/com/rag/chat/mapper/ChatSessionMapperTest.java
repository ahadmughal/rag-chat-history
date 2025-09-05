package com.rag.chat.mapper;

import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.entity.ChatSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatSessionMapperTest {

    private final ChatSessionMapper chatSessionMapper = new ChatSessionMapper();

    @Test
    void testToResponse() {
        ChatSession chatSession = new ChatSession();
        chatSession.setId(String.valueOf(1L));
        chatSession.setSessionName("Test Session");
        chatSession.setActive(true);
        chatSession.setFavorite(false);
        ChatSessionResponse response = chatSessionMapper.toResponse(chatSession);
        assertNotNull(response);
        assertEquals("1", response.getSessionId());
        assertEquals("Test Session", response.getSessionName());
        assertTrue(response.getActive());
        assertFalse(response.getFavorite());
    }
}