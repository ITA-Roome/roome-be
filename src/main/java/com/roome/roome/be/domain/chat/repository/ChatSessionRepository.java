package com.roome.roome.be.domain.chat.repository;

import com.roome.roome.be.domain.chat.model.ChatSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatSessionRepository {

    private static final String PREFIX = "chat:session:";
    private static final Duration TTL = Duration.ofHours(3);

    private final RedisTemplate<String, ChatSession> chatSessionRedisTemplate;

    public ChatSession create(String sessionId, Long userId) {
        ChatSession session = ChatSession.create(userId);
        save(sessionId, session);
        return session;
    }

    public void save(String sessionId, ChatSession session) {
        chatSessionRedisTemplate
                .opsForValue()
                .set(key(sessionId), session, TTL);
    }

    public Optional<ChatSession> find(String sessionId) {
        return Optional.ofNullable(
                chatSessionRedisTemplate
                        .opsForValue()
                        .get(key(sessionId))
        );
    }

    public void delete(String sessionId) {
        chatSessionRedisTemplate.delete(key(sessionId));
    }

    private String key(String sessionId) {
        return PREFIX + sessionId;
    }
}
