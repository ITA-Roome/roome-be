package com.roome.roome.be.common.redis;

import com.roome.roome.be.domain.chat.model.ChatSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisRepository redisRepository;

    public void save(ChatSession chatSession){
        redisRepository.save(chatSession);
    }

    public ChatSession get(Long userId) {
        return redisRepository.find(userId);
    }

    public void clear(Long userId) {
        redisRepository.delete(userId);
    }
}
