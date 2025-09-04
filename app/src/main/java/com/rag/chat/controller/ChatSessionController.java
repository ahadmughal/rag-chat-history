package com.rag.chat.controller;


import com.rag.chat.dto.request.CreateSessionRequest;
import com.rag.chat.dto.response.CreateSessionResponse;
import com.rag.chat.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chat Sessions", description = "Endpoints for managing chat sessions")
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Operation(summary = "Create a new chat session")
    @PostMapping("/create")
    public CreateSessionResponse createSession(@RequestBody CreateSessionRequest request) {
        return chatSessionService.createSession(request);
    }
}
