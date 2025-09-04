package com.rag.chat.service;

import com.rag.chat.dto.request.SendMessageRequest;
import com.rag.chat.dto.response.SendMessageResponse;

public interface ChatMessageService {
    SendMessageResponse sendMessage(SendMessageRequest request);
}
