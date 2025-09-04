package com.rag.chat.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSessionResponse {
    private Long sessionId;
    private String sessionName;
}
