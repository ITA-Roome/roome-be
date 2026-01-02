package com.roome.roome.be.domain.chat.repository;

import com.roome.roome.be.domain.chat.model.ChatSession;

import java.util.Optional;

public interface ChatSessionRepository {

    ChatSession save(ChatSession session);

    Optional<ChatSession> findById(String sessionId);

    ChatSession create(Long userId);

    void delete(String sessionId);
}

