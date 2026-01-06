package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.ai.service.AiIntentService;
import com.roome.roome.be.domain.chat.enums.ChatInputType;
import com.roome.roome.be.domain.chat.enums.ChatIntentType;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.enums.ChatTask;
import com.roome.roome.be.domain.chat.model.ChatDecision;
import com.roome.roome.be.domain.chat.model.ChatSession;
import com.roome.roome.be.domain.chat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final AiIntentService aiIntentService;

    public ChatDecision handle(Long userId, String sessionId, ChatInputType inputType, String message) {

        String key = sessionId != null ? sessionId : UUID.randomUUID().toString();

        ChatSession session = chatSessionRepository
                .find(key)
                .orElseGet(() -> chatSessionRepository.create(key, userId));

        log.info("현재 세션: {}", session);

        AiIntentResult intent = aiIntentService.analyze(session, message);
        log.info("의도 분석 결과: {}", intent);

        ChatSession updated = applyIntent(session, intent,message);

        chatSessionRepository.save(key, updated);

        return new ChatDecision(key, updated, intent);
    }

    /* =========================
       핵심: 의도 적용
    ========================= */
    private ChatSession applyIntent(ChatSession session, AiIntentResult intent,String message) {

        // 키워드 기반 도메인 선택 (가장 먼저!)
        if (session.mode() == ChatMode.UNDECIDED) {
            ChatMode keywordMode = resolveModeByKeyword(message);

            if (keywordMode == ChatMode.PRODUCT) {
                return session
                        .withMode(ChatMode.PRODUCT)
                        .withTask(ChatTask.PRODUCT_COLLECTING);
            }

            if (keywordMode == ChatMode.REFERENCE) {
                return session
                        .withMode(ChatMode.REFERENCE)
                        .withTask(ChatTask.REFERENCE_COLLECTING);
            }
        }
        // RESET
        if (intent.intent() == ChatIntentType.RESET) {
            return ChatSession.create(session.userId());
        }

        // 명시적 흐름 전환
        if (intent.intent() == ChatIntentType.CHANGE_FLOW) {
            return switchToResolvedMode(session, intent);
        }

        // SET_INFO / REQUEST_RECOMMEND / UNKNOWN
        boolean hasProductSignal = hasMeaningfulProductSignal(intent);
        boolean hasReferenceSignal = hasMeaningfulReferenceSignal(intent);

        // 3-1. 아직 도메인 미정
        if (session.mode() == ChatMode.UNDECIDED) {
            if (hasProductSignal) {
                return mergeProduct(session, intent);
            }
            if (hasReferenceSignal) {
                return mergeReference(session, intent);
            }
            return session;
        }

        // 3-2. 제품 수집 중
        if (session.mode() == ChatMode.PRODUCT) {
            if (hasReferenceSignal) {
                // 🔥 암묵적 전환
                return mergeReference(session.withMode(ChatMode.REFERENCE)
                        .withTask(ChatTask.REFERENCE_COLLECTING), intent);
            }
            return mergeProduct(session, intent);
        }

        // 3-3. 인테리어 수집 중
        if (session.mode() == ChatMode.REFERENCE) {
            if (hasProductSignal) {
                // 암묵적 전환
                return mergeProduct(session.withMode(ChatMode.PRODUCT)
                        .withTask(ChatTask.PRODUCT_COLLECTING), intent);
            }
            return mergeReference(session, intent);
        }

        return session;
    }

    /* =========================
       명시적 전환
    ========================= */
    private ChatSession switchToResolvedMode(ChatSession session, AiIntentResult intent) {
        if (hasProductSignal(intent)) {
            return session.withMode(ChatMode.PRODUCT)
                    .withTask(ChatTask.PRODUCT_COLLECTING);
        }
        if (hasReferenceSignal(intent)) {
            return session.withMode(ChatMode.REFERENCE)
                    .withTask(ChatTask.REFERENCE_COLLECTING);
        }
        return session;
    }

    /* =========================
       Slot Merge
    ========================= */
    private ChatSession mergeProduct(ChatSession session, AiIntentResult intent) {
        if (intent.product() == null) return session;

        var p = intent.product();

        return session.withProductInfo(
                mergeList(session.productTypes(), p.productTypes()),
                mergeList(session.productColors(), p.productColors()),
                firstNonNull(p.minBudget(), session.productMinBudget()),
                firstNonNull(p.maxBudget(), session.productMaxBudget())
        );
    }

    private ChatSession mergeReference(ChatSession session, AiIntentResult intent) {
        if (intent.reference() == null) return session;

        var r = intent.reference();

        return session.withReferenceInfo(
                firstNonNull(r.spaceType(), session.referenceType()),
                firstNonNull(r.spaceSize(), session.referenceSize()),
                mergeList(session.referenceMoods(), r.moods()),
                mergeList(session.referenceStyles(), r.styles()),
                firstNonNull(r.colorTone(), session.referenceColor()),
                firstNonNull(r.minBudget(), session.referenceMinBudget()),
                firstNonNull(r.maxBudget(), session.referenceMaxBudget())
        );
    }

    /* =========================
       Signal 판단
    ========================= */
    private boolean hasMeaningfulProductSignal(AiIntentResult intent) {
        if (intent.product() == null) return false;
        var p = intent.product();
        return (p.productTypes() != null && !p.productTypes().isEmpty())
                || (p.productColors() != null && !p.productColors().isEmpty())
                || p.minBudget() != null
                || p.maxBudget() != null;
    }

    private boolean hasMeaningfulReferenceSignal(AiIntentResult intent) {
        if (intent.reference() == null) return false;
        var r = intent.reference();
        return r.spaceType() != null
                || (r.moods() != null && !r.moods().isEmpty())
                || (r.styles() != null && !r.styles().isEmpty())
                || r.minBudget() != null
                || r.maxBudget() != null;
    }

    private boolean hasProductSignal(AiIntentResult intent) {
        return intent.product() != null;
    }

    private boolean hasReferenceSignal(AiIntentResult intent) {
        return intent.reference() != null;
    }

    private <T> T firstNonNull(T newValue, T oldValue) {
        return newValue != null ? newValue : oldValue;
    }

    private <T> List<T> mergeList(List<T> oldList, List<T> newList) {
        return (newList == null || newList.isEmpty()) ? oldList : newList;
    }

    private ChatMode resolveModeByKeyword(String message) {
        if (message == null) return null;

        if (message.contains("제품")
                || message.contains("상품")
                || message.contains("조명")
                || message.contains("가구")) {
            return ChatMode.PRODUCT;
        }

        if (message.contains("인테리어")
                || message.contains("방")
                || message.contains("공간")
                || message.contains("꾸미")) {
            return ChatMode.REFERENCE;
        }

        return null;
    }

}
