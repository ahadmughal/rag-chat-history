package com.rag.chat.mapper;

import com.rag.chat.dto.response.CreateSessionResponse;
import com.rag.chat.entity.ChatSession;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionMapper {

    public CreateSessionResponse toCreateSessionResponse(ChatSession session) {
        return CreateSessionResponse.builder()
                .sessionId(session.getId())
                .sessionName(session.getSessionName())
                .active(session.getActive())
                .build();
    }
}
