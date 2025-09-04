package com.rag.chat.service;

import com.rag.chat.dto.request.CreateSessionRequest;
import com.rag.chat.dto.response.CreateSessionResponse;

public interface ChatSessionService {
    CreateSessionResponse createSession(CreateSessionRequest request);
}
