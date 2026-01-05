package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.chat.dto.request.ChatMessageRequest;
import com.roome.roome.be.domain.chat.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatSessionService chatSessionService;

    public ChatMessageResponse handle(Long userId, ChatMessageRequest request) {
        return chatSessionService.handle(
                userId,
                request.sessionId(),
                request.inputType(),
                request.message()
        );
    }

}

