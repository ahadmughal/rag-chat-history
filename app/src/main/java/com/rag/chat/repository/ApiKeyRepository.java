package com.rag.chat.repository;

import com.rag.chat.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    // Find API key by its value
    Optional<ApiKey> findByKey(String key);
}
