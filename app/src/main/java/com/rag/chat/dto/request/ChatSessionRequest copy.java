package com.rag.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatSessionRequest {
    @NotBlank(message = "Session name is mandatory")
    private String sessionName;
}
