package com.rag.chat.dto.request;

import lombok.Data;

@Data
public class CreateSessionRequest {
    private Long userId;
    private String sessionName; // optional, default to "New Chat" if null
}
