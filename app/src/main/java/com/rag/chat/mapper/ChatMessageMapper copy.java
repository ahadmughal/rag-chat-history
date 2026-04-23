package com.rag.chat.mapper;

import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    /**
     * Maps a ChatMessage entity to a SendMessageResponse DTO.
     *
     * @param chatMessage The ChatMessage entity to be mapped
     * @return The corresponding SendMessageResponse DTO
     */
    public SendMessageResponse toSendMessageResponse(ChatMessage chatMessage) {
        SendMessageResponse response = new SendMessageResponse();
        response.setSessionId(chatMessage.getSession().getId().toString());
        response.setUserMessage(chatMessage.getContent());
        response.setResponseMessage(chatMessage.getContext());
        return response;
    }
}
