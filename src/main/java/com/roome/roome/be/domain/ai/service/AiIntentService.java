package com.roome.roome.be.domain.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.ai.prompt.IntentPromptBuilder;
import com.roome.roome.be.domain.chat.model.ChatSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiIntentService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    /**
     * 사용자 발화 + 현재 세션을 기반으로 의도 분석
     */
    public AiIntentResult analyze(ChatSession session, String userMessage) {

        // 프롬프트 생성
        String prompt = IntentPromptBuilder.build(session, userMessage);

        // OpenAI 호출
        String rawResponse = chatClient.call(prompt);

        // JSON 파싱
        return parseIntentResult(rawResponse);
    }

    /**
     * OpenAI 응답 JSON 파싱
     */
    private AiIntentResult parseIntentResult(String raw) {
        try {
            String json = extractJson(raw);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.AI_RESPONSE_NOT_PARSE);
        }
    }

    /**
     * GPT가 앞뒤에 설명 붙이는 경우 대비
     */
    private String extractJson(String raw) {
        int objStart = raw.indexOf("{");
        int objEnd = raw.lastIndexOf("}");

        if (objStart == -1 || objEnd == -1 || objEnd <= objStart) {
            throw new GeneralException(ErrorStatus.AI_RESPONSE_NOT_JSON);
        }

        return raw.substring(objStart, objEnd + 1);
    }
}
