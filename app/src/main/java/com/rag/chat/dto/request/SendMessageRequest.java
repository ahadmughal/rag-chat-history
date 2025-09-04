package com.rag.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Message cannot be empty")
    private String message;
}
