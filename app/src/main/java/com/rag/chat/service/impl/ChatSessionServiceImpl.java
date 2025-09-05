package com.rag.chat.service.impl;

import com.rag.chat.dto.request.ChatSessionRequest;
import com.rag.chat.dto.response.ChatSessionResponse;
import com.rag.chat.entity.ChatSession;
import com.rag.chat.mapper.ChatSessionMapper;
import com.rag.chat.repository.ChatMessageRepository;
import com.rag.chat.repository.ChatSessionRepository;
import com.rag.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.rag.chat.constants.AppConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

    private static final Logger logger = LoggerFactory.getLogger(ChatSessionServiceImpl.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatSessionMapper chatSessionMapper;

    /**
     * Creates a new chat session. Deactivates any existing active sessions.
     *
     * @param request The request body containing session details
     * @return The created chat session details
     * @throws IllegalArgumentException if the session name is null or blank
     */
    @Override
    public ChatSessionResponse createSession(ChatSessionRequest request) {
        if (request.getSessionName() == null || request.getSessionName().isBlank()) {
            throw new IllegalArgumentException(SESSION_NOT_EMPTY);
        }
        deactivateOldSessions();
        String sessionId = UUID.randomUUID().toString();
        ChatSession chatSession = buildSessionRequest(sessionId,  request.getSessionName());
        logger.info(CREATED_NEW_CHAT_SESSION, sessionId, request.getSessionName());
        return chatSessionMapper.toResponse(chatSession);
    }

    /**
     * Deactivates all currently active chat sessions.
     */
    private void deactivateOldSessions() {
        List<ChatSession> activeSessions = chatSessionRepository.findByActiveTrue();
        for (ChatSession session : activeSessions) {
            session.setActive(false);
            chatSessionRepository.save(session);
            logger.info(DEACTIVATED_OLD_SESSION, session.getId());
        }
    }

    /**
     * Builds and saves a new ChatSession entity.
     *
     * @param sessionId   The unique identifier for the session
     * @param sessionName The name of the session
     * @return The saved ChatSession entity
     */
    private ChatSession buildSessionRequest(String sessionId, String sessionName) {
        ChatSession session = ChatSession.builder()
                .id(sessionId)
                .sessionName(sessionName)
                .active(true)
                .favorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return chatSessionRepository.save(session);
    }

    /**
     * Retrieves all chat sessions, ordered by creation date descending.
     *
     * @return A list of all chat sessions
     */
    @Override
    public List<ChatSessionResponse> getAllSessions() {
        List<ChatSession> sessions = chatSessionRepository.findAllByOrderByCreatedAtDesc();
        return sessions.stream()
                .map(chatSessionMapper::toResponse)
                .toList();
    }

    /**
     * Toggles the favorite status of a chat session.
     *
     * @param sessionId The ID of the session to toggle favorite status
     * @return The updated chat session details
     * @throws IllegalStateException if the session with the given ID is not found
     */
    @Override
    public ChatSessionResponse markAsFavorite(String sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException(SESSION_NOT_FOUND_EXEC));
        session.setFavorite(!session.isFavorite());
        ChatSession updatedSession = chatSessionRepository.save(session);
        return chatSessionMapper.toResponse(updatedSession);
    }

    /**
     * Deletes a chat session and all associated messages.
     *
     * @param sessionId The ID of the session to delete
     * @throws IllegalStateException if the session with the given ID is not found
     */
    @Override
    @Transactional
    public void deleteSession(String sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException(SESSION_NOT_FOUND_EXEC));
        chatMessageRepository.deleteAllBySession(session);
        chatSessionRepository.delete(session);
        log.info(DELETED_SESSION_MESSAGES, sessionId);
    }

    /**
     * Retrieves the currently active chat session, if any.
     *
     * @return An Optional containing the active chat session details, or empty if none is active
     */
    @Override
    public Optional<ChatSessionResponse> getActiveSession() {
        List<ChatSession> activeSessions = chatSessionRepository.findByActiveTrue();
        if (activeSessions.isEmpty()) {
            return Optional.empty();
        }
        ChatSession session = activeSessions.get(0);
        ChatSessionResponse chatSessionResponse = chatSessionMapper.toResponse(session);
        return Optional.of(chatSessionResponse);
    }

    /**
     * Updates the name of an existing chat session.
     *
     * @param sessionId The ID of the session to update
     * @param newName   The new name for the session
     * @return The updated chat session details
     * @throws IllegalStateException if the session with the given ID is not found
     */
    @Override
    @Transactional
    public ChatSessionResponse updateSessionName(String sessionId, String newName) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException(SESSION_NOT_FOUND_EXEC));
        session.setSessionName(newName);
        session =  chatSessionRepository.save(session);
        return chatSessionMapper.toResponse(session);
    }
}
