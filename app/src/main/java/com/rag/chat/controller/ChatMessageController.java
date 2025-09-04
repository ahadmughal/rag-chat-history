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

@Tag(name = "Chat Messages", description = "Endpoints for managing chat messages")
@RestController
@RequestMapping("/messages")
public class ChatMessageController {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    @Autowired
    private ChatMessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<SendMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        logger.info("Received send message request for session {}", request.getSessionId());
        SendMessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getBy/{sessionId}")
    public ResponseEntity<List<SendMessageResponse>> getMessages(@PathVariable(required = true) String sessionId) {
        List<SendMessageResponse> messages = messageService.getMessagesBySession(sessionId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMessages(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String sessionId) {

        // Delegate all logic to the service
        List<ChatMessage> results = messageService.search(query, sessionId);
        return ResponseEntity.ok(results);
    }
}
