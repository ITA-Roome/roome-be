package com.roome.roome.be.domain.chat.model;
//
//import com.roome.roome.be.domain.chat.enums.ChatMode;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//
//@Getter
//public record ChatSession(
//        Long userId,
//        ChatMode chatMode,
//        Object request,
//        Object response,
//        LocalDateTime createdAt
//) {
//    public static ChatSession from(
//            Long userId,
//            ChatMode chatMode,
//            Object request,
//            Object response
//    ) {
//        return new ChatSession(
//                userId,
//                chatMode,
//                request,
//                response,
//                LocalDateTime.now()
//        );
//    }
//}

import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.reference.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatSession {
    private String sessionId;
    private Long userId;

    private ChatMode mode; // PRODUCT / REFERENCE (초기엔 null 가능)
    private int step;      // 단계 관리 (간단히 int로 시작)

    // Product slots
    private List<ProductType> productTypes = new ArrayList<>();
    private Integer minBudget;
    private Integer maxBudget;
    private List<String> preferredColors = new ArrayList<>();

    // Reference slots
    private ReferenceType referenceType;
    private ReferenceSize referenceSize;
    private ReferenceMood referenceMood;
    private ReferenceStyle referenceStyle;

    private Instant updatedAt = Instant.now();

    public static ChatSession create(String sessionId, Long userId) {
        ChatSession s = new ChatSession();
        s.sessionId = sessionId;
        s.userId = userId;
        s.step = 0;
        s.updatedAt = Instant.now();
        return s;
    }

    public void touch() { this.updatedAt = Instant.now(); }

    // ===== 추천 가능 조건(최소 조건) =====
    public boolean readyToRecommendProduct() {
        // productTypes가 비어도 된다면(“상관없어요”) 허용 가능
        // 여기서는 예산만 있어도 추천 가능하게 예시
        return (maxBudget != null || minBudget != null || !preferredColors.isEmpty() || !productTypes.isEmpty());
    }

    public boolean readyToRecommendReference() {
        // 최소한 타입/크기/무드/스타일 중 일부 + 예산 일부
        return (referenceType != null || referenceSize != null || referenceMood != null || referenceStyle != null)
                && (maxBudget != null || minBudget != null);
    }

}
