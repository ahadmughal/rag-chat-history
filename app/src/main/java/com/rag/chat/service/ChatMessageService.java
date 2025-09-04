package com.rag.chat.service;

import com.rag.chat.dto.request.SendMessageRequest;
import com.rag.chat.dto.response.SendMessageResponse;

import java.util.List;

public interface ChatMessageService {
    SendMessageResponse sendMessage(SendMessageRequest request);

    List<SendMessageResponse> getMessagesBySession(String sessionId);
}
