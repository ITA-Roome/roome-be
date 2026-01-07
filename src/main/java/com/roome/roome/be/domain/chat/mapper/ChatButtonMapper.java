package com.roome.roome.be.domain.chat.mapper;

import com.roome.roome.be.domain.chat.enums.ChatButtonAction;
import org.springframework.stereotype.Component;

@Component
public class ChatButtonMapper {

    public ChatButtonAction map(String message) {
        return switch (message) {

            case "제품 추천" -> ChatButtonAction.PRODUCT_FLOW;
            case "인테리어 추천" -> ChatButtonAction.REFERENCE_FLOW;

            case "가구" -> ChatButtonAction.PRODUCT_TYPE_FURNITURE;
            case "조명" -> ChatButtonAction.PRODUCT_TYPE_LIGHTING;
            case "패브릭" -> ChatButtonAction.PRODUCT_TYPE_FABRIC;

            case "10만원 이하" -> ChatButtonAction.PRODUCT_BUDGET_LOW;
            case "30만원 이하" -> ChatButtonAction.PRODUCT_BUDGET_MID;
            case "상관없어요" -> ChatButtonAction.PRODUCT_BUDGET_ANY;

            default -> throw new IllegalArgumentException("알 수 없는 버튼: " + message);
        };
    }
}

