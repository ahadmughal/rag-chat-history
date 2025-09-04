package com.rag.chat.mapper;

import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.entity.ChatSession;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionMapper {

    public ChatSessionResponse toCreateSessionResponse(ChatSession session) {
        return ChatSessionResponse.builder()
                .sessionId(session.getId())
                .sessionName(session.getSessionName())
                .active(session.getActive())
                .build();
    }

    public ChatSessionResponse toResponse(ChatSession session) {
        return ChatSessionResponse.builder()
                .sessionId(session.getId())
                .sessionName(session.getSessionName())
                .active(session.getActive())
                .favorite(session.isFavorite())
                .build();
    }
}
