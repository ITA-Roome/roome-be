package com.roome.roome.be.domain.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.ai.dto.request.AiProductRequest;
import com.roome.roome.be.domain.ai.dto.request.AiReferenceRequest;
import com.roome.roome.be.domain.ai.dto.response.AiProductResponse;
import com.roome.roome.be.domain.ai.dto.response.AiReferenceResponse;
import com.roome.roome.be.domain.ai.prompt.ProductPromptBuilder;
import com.roome.roome.be.domain.ai.prompt.ReferencePromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    /* =========================
       상품 추천
    ========================= */
    public List<AiProductResponse> recommendProductList(AiProductRequest request) {
        String prompt = ProductPromptBuilder.build(request);
        String raw = chatClient.call(prompt);
        return parse(raw, new TypeReference<>() {});
    }

    /* =========================
       레퍼런스 추천
    ========================= */
    public AiReferenceResponse recommendReferenceList(AiReferenceRequest request) {
        String prompt = ReferencePromptBuilder.build(request);
        String raw = chatClient.call(prompt);
        return parse(raw, AiReferenceResponse.class);
    }

    /* =========================
       공통 파서 (핵심)
    ========================= */
    private <T> T parse(String raw, Class<T> clazz) {
        try {
            String json = extractJson(raw);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.AI_RESPONSE_NOT_PARSE);
        }
    }

    private <T> T parse(String raw, TypeReference<T> typeRef) {
        try {
            String json = extractJson(raw);
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.AI_RESPONSE_NOT_PARSE);
        }
    }

    private String extractJson(String raw) {
        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");

        if (start < 0 || end < 0 || start >= end) {
            throw new GeneralException(ErrorStatus.AI_RESPONSE_NOT_JSON);
        }

        return raw.substring(start, end + 1);
    }
}
