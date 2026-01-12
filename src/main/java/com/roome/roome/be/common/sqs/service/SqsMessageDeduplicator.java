package com.roome.roome.be.common.sqs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SqsMessageDeduplicator {

    private final StringRedisTemplate stringRedisTemplate;

    private static final Duration TTL = Duration.ofMinutes(10);

    private static String key(String messageId) {
        return "sqs:processed:" + messageId;
    }

    public boolean alreadyProcessed(String messageId) {
        Boolean exists = stringRedisTemplate.hasKey(key(messageId));
        return Boolean.TRUE.equals(exists);
    }

    public void markProcessed(String messageId) {
        stringRedisTemplate
                .opsForValue()
                .set(key(messageId), "1", TTL);
    }
}
