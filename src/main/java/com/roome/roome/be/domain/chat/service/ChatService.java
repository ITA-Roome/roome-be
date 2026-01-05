package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.chat.dto.request.ChatMessageRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatProductScenarioRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.*;
import com.roome.roome.be.domain.chat.enums.ChatIntentType;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.model.ChatDecision;
import com.roome.roome.be.domain.chat.model.ChatSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatSessionService chatSessionService;
    private final ChatRecommendService chatRecommendService;

    public ChatMessageResponse handle(Long userId, ChatMessageRequest request) {
        ChatDecision decision = chatSessionService.handle(
                userId,
                request.sessionId(),
                request.inputType(),
                request.message()
        );

        return decideNextAction(decision.sessionId(),decision.session(),decision.intent());
    }

    // 의도에 맞춰서 행동 수행
    private ChatMessageResponse decideNextAction(
            String sessionId,
            ChatSession session,
            AiIntentResult intent
    ) {

        // 초기화 요청인 경우
        if (intent.intent() == ChatIntentType.RESET) {
            return ChatMessageResponse.question(
                    sessionId,
                    "처음부터 다시 시작할게요 🙂\n무엇을 도와드릴까요?",
                    List.of("방 인테리어 추천", "제품 추천")
            );
        }

        // 추천 요청인 경우
        else if (intent.intent() == ChatIntentType.REQUEST_RECOMMEND) {

            // 제품인 경우
            if (session.mode() == ChatMode.PRODUCT) {
                if (!session.canRecommendProduct()) {
                    return askNextProductQuestion(sessionId,session);
                }

                return ChatMessageResponse.result(
                        sessionId,
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
                    return askNextReferenceQuestion(sessionId, session);
                }

                return ChatMessageResponse.result(
                        sessionId,
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
                    ? askNextProductQuestion(sessionId, session)
                    : askNextReferenceQuestion(sessionId, session);
        }

        // 기타
        return ChatMessageResponse.question(
                sessionId,
                "조금만 더 자세히 알려주세요 🙂",
                List.of("제품 추천", "인테리어 추천")
        );
    }

    /* =========================
       Question Builders
    ========================= */
    private ChatMessageResponse askNextProductQuestion(String sessionId, ChatSession session) {

        if (session.productTypes() == null || session.productTypes().isEmpty()) {
            return ChatMessageResponse.question(
                    sessionId,
                    "어떤 종류의 제품을 찾고 계신가요?",
                    List.of("가구", "조명", "패브릭")
            );
        }

        if (session.productMinBudget() == null && session.productMaxBudget() == null) {
            return ChatMessageResponse.question(
                    sessionId,
                    "예산은 어느 정도로 생각하고 계신가요?",
                    List.of("10만원 이하", "30만원 이하", "상관없어요")
            );
        }

        return ChatMessageResponse.question(
                sessionId,
                "이제 추천해드릴 수 있어요!",
                List.of("추천해줘")
        );
    }

    private ChatMessageResponse askNextReferenceQuestion(String sessionId, ChatSession session) {

        if (session.referenceType() == null) {
            return ChatMessageResponse.question(
                    sessionId,
                    "어떤 공간을 꾸미고 싶으신가요?",
                    List.of("거실", "침실", "서재")
            );
        }

        if (session.referenceMoods() == null || session.referenceMoods().isEmpty()) {
            return ChatMessageResponse.question(
                    sessionId,
                    "어떤 분위기를 원하시나요?",
                    List.of("차분한", "아늑한", "모던한")
            );
        }

        if (session.referenceMinBudget() == null && session.referenceMaxBudget() == null) {
            return ChatMessageResponse.question(
                    sessionId,
                    "예산은 어느 정도 생각하고 계세요?",
                    List.of("50만원 이하", "100만원 이하", "상관없어요")
            );
        }

        return ChatMessageResponse.question(
                sessionId,
                "이제 인테리어 추천을 해드릴게요 🙂",
                List.of("추천해줘")
        );
    }


}

