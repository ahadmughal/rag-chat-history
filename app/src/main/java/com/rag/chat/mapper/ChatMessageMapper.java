package com.rag.chat.mapper;

import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public SendMessageResponse toSendMessageResponse(ChatMessage chatMessage) {
        SendMessageResponse response = new SendMessageResponse();
        response.setSessionId(chatMessage.getSession().getId().toString());
        response.setUserMessage(chatMessage.getSender());
        response.setResponseMessage(chatMessage.getContext());
        return response;
    }
}
