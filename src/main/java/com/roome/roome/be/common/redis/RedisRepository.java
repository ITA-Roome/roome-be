package com.roome.roome.be.common.redis;

import com.roome.roome.be.domain.chat.model.ChatSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "chat:session:";

    public void save(ChatSession chatSession) {
        redisTemplate.opsForValue()
                .set(PREFIX + chatSession.userId(), chatSession, Duration.ofHours(3));
    }

    public ChatSession find(Long userId) {
        return (ChatSession) redisTemplate.opsForValue().get(PREFIX + userId);
    }

    public void delete(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}
