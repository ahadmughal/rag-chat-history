package com.rag.chat.mapper;

import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.entity.ChatSession;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionMapper {

    /**
     * Maps a ChatSession entity to a ChatSessionResponse DTO.
     *
     * @param session The ChatSession entity to be mapped
     * @return The corresponding ChatSessionResponse DTO
     */
    public ChatSessionResponse toResponse(ChatSession session) {
        return ChatSessionResponse.builder()
                .sessionId(session.getId())
                .sessionName(session.getSessionName())
                .active(session.getActive())
                .favorite(session.isFavorite())
                .build();
    }

}
