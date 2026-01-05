package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.ai.service.AiIntentService;
import com.roome.roome.be.domain.chat.enums.ChatInputType;
import com.roome.roome.be.domain.chat.enums.ChatMode;
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

        // 세션 불러오기
        ChatSession session = chatSessionRepository.
                find(key).
                orElseGet(()-> chatSessionRepository.create(key, userId));
        log.info("현재 세션 값 {}", session);

        // 현재 Session 상태와 사용자의 메시지를 분석해서 의도 분석(추천 받을려는 제품의 정보를 입력하는지, 추천 결과를 받을려는 것인지 기타 등등)
        AiIntentResult intent = aiIntentService.analyze(session, message);
        log.error("의도 분석 {}", intent);

        // 사용자의 의도 분석 후 ChatSession 업데이트
        ChatSession updatedSession = applyIntent(session, intent);
        chatSessionRepository.save(key, updatedSession);

        // 그 다음 로직 수행
        return new ChatDecision(key,updatedSession, intent);
    }



    private ChatSession applyIntent(ChatSession session, AiIntentResult intent) {

        return switch (intent.intent()) {
            case RESET -> ChatSession.create(session.userId());

            case CHANGE_FLOW -> session.withMode(resolveMode(intent));

            case SET_INFO -> mergeSlots(session, intent);

            case REQUEST_RECOMMEND -> session;

            default -> session;
        };
    }

    /* =========================
       Slot Merge
    ========================= */
    private ChatSession mergeSlots(ChatSession session, AiIntentResult intent) {

        ChatSession result = session;

        if (intent.product() != null) {
            var p = intent.product();

            result = result.withProductInfo(
                    mergeList(session.productTypes(), p.productTypes()),
                    mergeList(session.productColors(), p.productColors()),
                    firstNonNull(p.minBudget(), session.productMinBudget()),
                    firstNonNull(p.maxBudget(), session.productMaxBudget())
            );
        }

        if (intent.reference() != null) {
            var r = intent.reference();

            result = result.withReferenceInfo(
                    firstNonNull(r.spaceType(), session.referenceType()),
                    firstNonNull(r.spaceSize(), session.referenceSize()),
                    mergeList(session.referenceMoods(), r.moods()),
                    mergeList(session.referenceStyles(), r.styles()),
                    firstNonNull(r.colorTone(), session.referenceColor()),
                    firstNonNull(r.minBudget(), session.referenceMinBudget()),
                    firstNonNull(r.maxBudget(), session.referenceMaxBudget())
            );
        }

        return result;
    }

    private <T> T firstNonNull(T newValue, T oldValue) {
        return newValue != null ? newValue : oldValue;
    }

    private <T> List<T> mergeList(List<T> oldList, List<T> newList) {
        return (newList == null || newList.isEmpty()) ? oldList : newList;
    }

    /* =========================
       Response Decision
    ========================= */
    private ChatMode resolveMode(AiIntentResult intent) {
        return intent.product() != null
                ? ChatMode.PRODUCT
                : ChatMode.REFERENCE;
    }

}
