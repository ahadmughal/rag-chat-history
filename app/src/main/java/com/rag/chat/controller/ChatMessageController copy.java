package com.rag.chat.controller;

import com.rag.chat.dto.request.SendMessageRequest;
import com.rag.chat.dto.response.SendMessageResponse;
import com.rag.chat.entity.ChatMessage;
import com.rag.chat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.rag.chat.constants.AppConstants.SEND_MESSAGE_REQUEST_LOG;

@Tag(name = "Chat Messages", description = "Endpoints for managing chat messages")
@RestController
@RequestMapping("/messages")
public class ChatMessageController {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    @Autowired
    private ChatMessageService messageService;

    /**
     * Endpoint to send a message in a chat session.
     *
     * @param request The request body containing message details
     * @return The response containing the sent message details
     */
    @PostMapping("/send")
    public ResponseEntity<SendMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        logger.info(SEND_MESSAGE_REQUEST_LOG, request.getSessionId());
        SendMessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve all messages for a specific chat session.
     *
     * @param sessionId The ID of the chat session
     * @return A list of messages in the specified chat session
     */
    @GetMapping("/getBy/{sessionId}")
    public ResponseEntity<List<SendMessageResponse>> getMessages(@PathVariable String sessionId) {
        List<SendMessageResponse> messages = messageService.getMessagesBySession(sessionId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Endpoint to search messages based on a query and optional session ID.
     *
     * @param query     The search query (optional)
     * @param sessionId The ID of the chat session (optional)
     * @return A list of messages matching the search criteria
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchMessages(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String sessionId) {
        List<ChatMessage> results = messageService.search(query, sessionId);
        return ResponseEntity.ok(results);
    }
}
