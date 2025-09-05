package com.rag.chat.mapper;

import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import com.rag.chat.entity.ChatSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageMapperTest {

    private final ChatMessageMapper chatMessageMapper = new ChatMessageMapper();

    @Test
    void testToSendMessageResponse() {
        ChatSession chatSession = new ChatSession();
        chatSession.setId(String.valueOf(1L));
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSession(chatSession);
        chatMessage.setContent("User message");
        chatMessage.setContext("Response message");
        SendMessageResponse response = chatMessageMapper.toSendMessageResponse(chatMessage);
        assertNotNull(response);
        assertEquals("1", response.getSessionId());
        assertEquals("User message", response.getUserMessage());
        assertEquals("Response message", response.getResponseMessage());
    }
}