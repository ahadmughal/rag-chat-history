package com.rag.chat.repository;

import com.rag.chat.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {

    // Find all active sessions
    List<ChatSession> findByActiveTrue();

}
