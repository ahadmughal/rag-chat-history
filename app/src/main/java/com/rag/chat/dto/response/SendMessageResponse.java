package com.rag.chat.dto.response;

import lombok.Data;

@Data
public class SendMessageResponse {
    private String sessionId;
    private String userMessage;
    private String responseMessage;
}
