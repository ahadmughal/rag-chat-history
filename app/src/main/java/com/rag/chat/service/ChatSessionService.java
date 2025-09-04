package com.rag.chat.service;

import com.rag.chat.dto.request.ChatSessionRequest;
import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.entity.ChatSession;

import java.util.List;

public interface ChatSessionService {
    ChatSessionResponse createSession(ChatSessionRequest request);
    List<ChatSessionResponse> getAllSessions();
    ChatSessionResponse markAsFavorite(String sessionId);
    void deleteSession(String sessionId);
}
