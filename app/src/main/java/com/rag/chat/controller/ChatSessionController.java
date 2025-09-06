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

    /** Create a new chat session
     *
     * @param request The request body containing session details
     * @return The created chat session details
     */
    @Operation(summary = "Create a new chat session")
    @PostMapping("/create")
    public ChatSessionResponse createSession(@RequestBody ChatSessionRequest request) {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);
        log.info(CREATE_SESSION_REQUEST, request.getSessionName());
        ChatSessionResponse response = chatSessionService.createSession(request);
        log.info(SESSION_CREATED_SUCCESSFULLY, response.getSessionId(), response.getSessionName());
        MDC.clear();
        return response;
    }

    /** Update the name of an existing chat session
     *
     * @param sessionId The ID of the session to update
     * @param request   The request body containing the new session name
     * @return The updated chat session details
     */
    @PatchMapping("/{sessionId}/name")
    public ResponseEntity<ChatSessionResponse> updateSessionName(
            @PathVariable String sessionId,
            @RequestBody ChatSessionRequest request) {
        ChatSessionResponse updatedSession = chatSessionService.updateSessionName(sessionId, request.getSessionName());
        return ResponseEntity.ok(updatedSession);
    }

    /** Retrieve all chat sessions
     *
     * @return A list of all chat sessions
     */
    @GetMapping
    public List<ChatSessionResponse> getAllSessions() {
        return chatSessionService.getAllSessions();
    }

    /** Mark or unmark a chat session as favorite
     *
     * @param sessionId The ID of the session to toggle favorite status
     * @return The updated chat session details
     */
    @PostMapping("/toggle/favorite/{sessionId}")
    public ResponseEntity<ChatSessionResponse> markSessionAsFavorite(@PathVariable String sessionId) {
        ChatSessionResponse updatedSessionResponse = chatSessionService.markAsFavorite(sessionId);
        return ResponseEntity.ok(updatedSessionResponse);
    }

    /** Delete a chat session
     *
     * @param sessionId The ID of the session to delete
     * @return A response entity with no content
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        chatSessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    /** Retrieve the currently active chat session
     *
     * @return The active chat session details, or 404 if none is active
     */
    @GetMapping("/active")
    public ResponseEntity<ChatSessionResponse> getActiveSession() {
        return chatSessionService.getActiveSession()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
