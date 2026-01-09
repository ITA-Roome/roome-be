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

        // 1. AI 의도 분석
        AiIntentResult intent = aiIntentService.analyze(session, message);
        log.info("의도 분석 결과: {}", intent);

        // 2. 세션 업데이트 (Merge)
        ChatSession updated = applyIntent(session, intent, message);

        chatSessionRepository.save(key, updated);

        return new ChatDecision(key, updated, intent);
    }

    /* =========================
       핵심: 의도 적용
    ========================= */
    private ChatSession applyIntent(ChatSession session, AiIntentResult intent, String message) {

        // 1. 키워드 기반 도메인 강제 진입 (첫 진입 시 혹은 UNDECIDED 일때만)
        if (session.mode() == ChatMode.UNDECIDED) {
            ChatMode keywordMode = resolveModeByKeyword(message);

            if (keywordMode == ChatMode.PRODUCT) {
                return session.withMode(ChatMode.PRODUCT).withTask(ChatTask.PRODUCT_COLLECTING);
            }
            if (keywordMode == ChatMode.REFERENCE) {
                return session.withMode(ChatMode.REFERENCE).withTask(ChatTask.REFERENCE_COLLECTING);
            }
        }

        // 2. RESET 처리
        if (intent.intent() == ChatIntentType.RESET) {
            return ChatSession.create(session.userId());
        }

        // 3. 명시적 흐름 전환 (CHANGE_FLOW) - AI가 "전환"이라고 판단했을 때만 실행
        if (intent.intent() == ChatIntentType.CHANGE_FLOW) {
            return switchToResolvedMode(session, intent);
        }

        // 4. 정보 병합 (SET_INFO / REQUEST_RECOMMEND / UNKNOWN)

        // 4-1. 제품 수집 중 (PRODUCT)
        if (session.mode() == ChatMode.PRODUCT) {
            return mergeProduct(session, intent);
        }

        // 4-2. 인테리어 수집 중 (REFERENCE)
        if (session.mode() == ChatMode.REFERENCE) {
            return mergeReference(session, intent);
        }

        // 4-3. 아직 도메인 미정 (UNDECIDED)
        boolean hasProductSignal = hasMeaningfulProductSignal(intent);
        boolean hasReferenceSignal = hasMeaningfulReferenceSignal(intent);

        if (hasProductSignal) {
            return mergeProduct(session.withMode(ChatMode.PRODUCT)
                    .withTask(ChatTask.PRODUCT_COLLECTING), intent);
        }
        if (hasReferenceSignal) {
            return mergeReference(session.withMode(ChatMode.REFERENCE)
                    .withTask(ChatTask.REFERENCE_COLLECTING), intent);
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
       Slot Merge (데이터 병합)
    ========================= */
    private ChatSession mergeProduct(ChatSession session, AiIntentResult intent) {
        // 1. AI가 채워준 Product 정보 가져오기
        var p = intent.product();
        // 2. 혹시 AI가 Reference 쪽에 예산을 넣었는지 확인하기
        var r = intent.reference();

        Integer finalMin = (p != null && p.minBudget() != null) ? p.minBudget()
                : (r != null ? r.minBudget() : null);

        Integer finalMax = (p != null && p.maxBudget() != null) ? p.maxBudget()
                : (r != null ? r.maxBudget() : null);

        // 3. 값 적용
        return session.withProductInfo(
                p != null ? firstNonNull(p.productType(), session.productType()) : session.productType(),
                p != null ? mergeList(session.productCategories(), p.productCategories()) : session.productCategories(),
                p != null ? mergeList(session.productColors(), p.productColors()) : session.productColors(),
                firstNonNull(finalMin, session.productMinBudget()), // 보정된 값 사용
                firstNonNull(finalMax, session.productMaxBudget())  // 보정된 값 사용
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
       Signal 판단 (유의미한 정보가 있는지)
    ========================= */
    private boolean hasMeaningfulProductSignal(AiIntentResult intent) {
        if (intent.product() == null) return false;
        var p = intent.product();

        return p.productType() != null
                || (p.productCategories() != null && !p.productCategories().isEmpty())
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
                || message.contains("가구")
                || message.contains("커튼")
                || message.contains("블라인드")) {
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