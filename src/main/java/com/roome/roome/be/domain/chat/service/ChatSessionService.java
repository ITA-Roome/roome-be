package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.ai.service.AiIntentService;
import com.roome.roome.be.domain.chat.dto.request.ChatProductScenarioRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.ChatMessageResponse;
import com.roome.roome.be.domain.chat.enums.ChatInputType;
import com.roome.roome.be.domain.chat.enums.ChatIntentType;
import com.roome.roome.be.domain.chat.enums.ChatMode;
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

    private final ChatRecommendService chatRecommendService;
    private final ChatSessionRepository sessionRepository;
    private final AiIntentService aiIntentService;

    public ChatMessageResponse handle(Long userId, String sessionId, ChatInputType inputType, String message) {

        // 세션 불러오기
        ChatSession session = loadOrCreateSession(userId, sessionId);
        log.error("현재 세션 값 {}", session);

        // 현재 Session 상태와 사용자의 메시지를 분석해서 의도 분석(추천 받을려는 제품의 정보를 입력하는지, 추천 결과를 받을려는 것인지 기타 등등)
        AiIntentResult intent = aiIntentService.analyze(session, message);
        log.error("의도 분석 {}", intent);

        // 사용자의 의도 분석 후 ChatSession 업데이트
        ChatSession updatedSession = applyIntent(session, intent);
        sessionRepository.save(updatedSession);

        // 그 다음 로직 수행
        return decideNextResponse(updatedSession, intent);
    }

    /* =========================
       Session Handling
    ========================= */

    private ChatSession loadOrCreateSession(Long userId, String sessionId) {

        if (sessionId == null) {
            return ChatSession.create(
                    UUID.randomUUID().toString(),
                    userId
            );
        }

        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("세션이 존재하지 않습니다."));
    }


    private ChatSession applyIntent(ChatSession session, AiIntentResult intent) {

        return switch (intent.intent()) {
            case RESET -> sessionRepository.create(session.userId());

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

    private ChatMessageResponse decideNextResponse(
            ChatSession session,
            AiIntentResult intent
    ) {

        // 초기화 요청인 경우
        if (intent.intent() == ChatIntentType.RESET) {
            return ChatMessageResponse.question(
                    session.sessionId(),
                    "처음부터 다시 시작할게요 🙂\n무엇을 도와드릴까요?",
                    List.of("방 인테리어 추천", "제품 추천")
            );
        }

        // 추천 요청인 경우
        else if (intent.intent() == ChatIntentType.REQUEST_RECOMMEND) {

            // 제품인 경우
            if (session.mode() == ChatMode.PRODUCT) {
                if (!session.canRecommendProduct()) {
                    return askNextProductQuestion(session);
                }

                return ChatMessageResponse.result(
                        session.sessionId(),
                        "조건에 맞는 제품을 추천해드릴게요!",
                        List.of("추천 결과 보기"),
                        chatRecommendService.processChatProductScenario(
                                ChatProductScenarioRequest.create(session)
                        )
                );
            }

            // 인테리어인 경우
            else if (session.mode() == ChatMode.REFERENCE) {
                if (!session.canRecommendReference()) {
                    return askNextReferenceQuestion(session);
                }

                return ChatMessageResponse.result(
                        session.sessionId(),
                        "인테리어 추천을 준비했어요 🙂",
                        List.of("추천 결과 보기"),
                        chatRecommendService.processChatReferenceScenario(
                                ChatReferenceScenarioRequest.create(session)
                        )
                );
            }
        }

        // 추가적인 정보 입력
        else if (intent.intent() == ChatIntentType.SET_INFO) {
            return session.mode() == ChatMode.PRODUCT
                    ? askNextProductQuestion(session)
                    : askNextReferenceQuestion(session);
        }

        // 기타
        return ChatMessageResponse.question(
                session.sessionId(),
                "조금만 더 자세히 알려주세요 🙂",
                List.of("제품 추천", "인테리어 추천")
        );
    }

    /* =========================
       Question Builders
    ========================= */
    private ChatMessageResponse askNextProductQuestion(ChatSession session) {

        if (session.productTypes() == null || session.productTypes().isEmpty()) {
            return ChatMessageResponse.question(
                    session.sessionId(),
                    "어떤 종류의 제품을 찾고 계신가요?",
                    List.of("가구", "조명", "패브릭")
            );
        }

        if (session.productMinBudget() == null && session.productMaxBudget() == null) {
            return ChatMessageResponse.question(
                    session.sessionId(),
                    "예산은 어느 정도로 생각하고 계신가요?",
                    List.of("10만원 이하", "30만원 이하", "상관없어요")
            );
        }

        return ChatMessageResponse.question(
                session.sessionId(),
                "이제 추천해드릴 수 있어요!",
                List.of("추천해줘")
        );
    }

    private ChatMessageResponse askNextReferenceQuestion(ChatSession session) {

        if (session.referenceType() == null) {
            return ChatMessageResponse.question(
                    session.sessionId(),
                    "어떤 공간을 꾸미고 싶으신가요?",
                    List.of("거실", "침실", "서재")
            );
        }

        if (session.referenceMoods() == null || session.referenceMoods().isEmpty()) {
            return ChatMessageResponse.question(
                    session.sessionId(),
                    "어떤 분위기를 원하시나요?",
                    List.of("차분한", "아늑한", "모던한")
            );
        }

        if (session.referenceMinBudget() == null && session.referenceMaxBudget() == null) {
            return ChatMessageResponse.question(
                    session.sessionId(),
                    "예산은 어느 정도 생각하고 계세요?",
                    List.of("50만원 이하", "100만원 이하", "상관없어요")
            );
        }

        return ChatMessageResponse.question(
                session.sessionId(),
                "이제 인테리어 추천을 해드릴게요 🙂",
                List.of("추천해줘")
        );
    }

    private ChatMode resolveMode(AiIntentResult intent) {
        return intent.product() != null
                ? ChatMode.PRODUCT
                : ChatMode.REFERENCE;
    }
}
