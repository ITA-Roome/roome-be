package com.roome.roome.be.domain.chat.repository;

import com.roome.roome.be.domain.chat.model.ChatSession;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryChatSessionRepository implements ChatSessionRepository {
    private final Map<String, ChatSession> store = new ConcurrentHashMap<>();

    public String newSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public ChatSession save(ChatSession session) {
        store.put(session.getSessionId(), session);
        return session;
    }

    @Override
    public Optional<ChatSession> findById(String sessionId) {
        return Optional.ofNullable(store.get(sessionId));
    }

    @Override
    public void delete(String sessionId) {
        store.remove(sessionId);
    }

}
