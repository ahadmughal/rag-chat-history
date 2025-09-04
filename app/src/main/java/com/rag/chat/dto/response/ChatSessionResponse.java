package com.rag.chat.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSessionResponse {
    private String sessionId;
    private String sessionName;
    private Boolean active;
    private Boolean favorite;
}
