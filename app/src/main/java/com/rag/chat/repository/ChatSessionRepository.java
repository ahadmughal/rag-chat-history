package com.rag.chat.repository;

import com.rag.chat.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    // Find all sessions by userId
    List<ChatSession> findByUserId(Long userId);

    // Find favorite sessions for a user
    List<ChatSession> findByUserIdAndIsFavoriteTrue(Long userId);
}
