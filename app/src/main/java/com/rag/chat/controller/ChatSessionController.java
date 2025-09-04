package com.rag.chat.controller;

import com.rag.chat.dto.request.ChatSessionRequest;
import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.mapper.ChatSessionMapper;
import com.rag.chat.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.rag.chat.constants.AppConstants.*;

@Tag(name = "Chat Sessions", description = "Endpoints for managing chat sessions")
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Slf4j
public class ChatSessionController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Operation(summary = "Create a new chat session")
    @PostMapping("/create")
    public ChatSessionResponse createSession(@RequestBody ChatSessionRequest request,
                                             HttpServletRequest httpRequest) {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);
        log.info(CREATE_SESSION_REQUEST, request.getSessionName());
        try {
            ChatSessionResponse response = chatSessionService.createSession(request);
            log.info(SESSION_CREATED_SUCCESSFULLY, response.getSessionId(), response.getSessionId());
            return response;
        } catch (Exception e) {
            log.error(ERROR_CREATING_SESSION, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{sessionId}/name")
    public ResponseEntity<ChatSessionResponse> updateSessionName(
            @PathVariable String sessionId,
            @RequestBody ChatSessionRequest request) {
        ChatSessionResponse updatedSession = chatSessionService.updateSessionName(sessionId, request.getSessionName());
        return ResponseEntity.ok(updatedSession);
    }

    @GetMapping
    public List<ChatSessionResponse> getAllSessions() {
        return chatSessionService.getAllSessions();
    }

    @PostMapping("/toggle/favorite/{sessionId}")
    public ResponseEntity<ChatSessionResponse> markSessionAsFavorite(@PathVariable String sessionId) {
        ChatSessionResponse updatedSessionResponse = chatSessionService.markAsFavorite(sessionId);
        return ResponseEntity.ok(updatedSessionResponse);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        chatSessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<ChatSessionResponse> getActiveSession() {
        return chatSessionService.getActiveSession()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
