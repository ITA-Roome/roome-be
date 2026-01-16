package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.chat.dto.request.ChatMessageRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatProductScenarioRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.*;
import com.roome.roome.be.domain.chat.enums.ChatIntentType;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.enums.MissingField;
import com.roome.roome.be.domain.chat.model.ChatDecision;
import com.roome.roome.be.domain.chat.model.ChatSession;
import com.roome.roome.be.domain.chat.repository.ChatSessionRepository;
import com.roome.roome.be.domain.product.enums.ProductType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;

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

        if (intent.intent() == ChatIntentType.RESET) {
            return ChatMessageResponse.question(
                    sessionId,
                    "처음부터 다시 시작할게요 🙂\n무엇을 도와드릴까요?",
                    List.of("방 인테리어 추천", "제품 추천")
            );
        }

        if (session.mode() == ChatMode.PRODUCT) {

            if (!session.isProductReady()) {
                return askProductByMissingField(sessionId, session);
            }

            ChatProductScenarioResponse resultData = chatRecommendService.processChatProductScenario(
                    ChatProductScenarioRequest.create(session)
            );
            ChatSession updatedSession = session.toBuilder()
                    .lastProductResult(resultData)
                    .build();

            chatSessionRepository.save(sessionId, updatedSession);

            return ChatMessageResponse.result(
                    sessionId,
                    "조건에 맞는 제품을 추천해드릴게요!",
                    List.of("추천 결과 보기"),
                    resultData
            );
        }

        if (session.mode() == ChatMode.REFERENCE) {

            if (!session.isReferenceReady()) {
                return askReferenceByMissingField(sessionId, session);
            }

            ChatReferenceScenarioResponse resultData = chatRecommendService.processChatReferenceScenario(
                    ChatReferenceScenarioRequest.create(session)
            );

            ChatSession updatedSession = session.toBuilder()
                    .lastReferenceResult(resultData)
                    .build();

            chatSessionRepository.save(sessionId, updatedSession);

            return ChatMessageResponse.result(
                    sessionId,
                    "인테리어 추천을 준비했어요 🙂",
                    List.of("추천 결과 보기"),
                    resultData
            );
        }

        return ChatMessageResponse.question(
                sessionId,
                "어떤 도움을 드릴까요?",
                List.of("제품 추천", "인테리어 추천")
        );
    }

    private ChatMessageResponse askProductByMissingField(
            String sessionId,
            ChatSession session
    ) {
        MissingField field = session.missingProductFields().get(0);

        return switch (field) {

            case PRODUCT_TYPE -> ChatMessageResponse.question(
                    sessionId,
                    "어떤 종류의 제품을 찾고 계신가요?",
                    List.of("가구", "조명", "패브릭 & 데코", "커튼 & 블라인드")
            );

            case PRODUCT_DETAIL_CATEGORY -> {
                ProductType type = session.productType();

                List<String> buttons = switch (type) {
                    case FURNITURE -> List.of("책상", "테이블", "의자", "스툴");
                    case LIGHTING -> List.of("천장등", "독서등", "장식 조명");
                    case FABRIC_DECOR -> List.of("러그", "쿠션", "아트 프린트");
                    case WINDOW -> List.of("커튼", "블라인드", "쿠션");
                    default -> List.of("전체 보기");
                };

                yield ChatMessageResponse.question(
                        sessionId,
                        "구체적으로 어떤 제품을 찾으세요?",
                        buttons
                );
            }

            //  예산 질문
            case PRODUCT_BUDGET -> ChatMessageResponse.question(
                    sessionId,
                    "가격대는 어느 정도면 좋을까요?\n(예: 20만원 이하, 조금 비싸도 괜찮아요)",
                    List.of("가성비", "중간 가격대", "가격 무관")
            );

            //  컬러/분위기 질문
            case PRODUCT_COLOR_MOOD -> ChatMessageResponse.question(
                    sessionId,
                    "선호하는 컬러나 분위기 취향이 있으신가요?\n(예: 화이트+우드, 차분한 느낌)",
                    List.of("밝은 계열", "어두운 계열", "상관없어요")
            );

            default -> ChatMessageResponse.question(
                    sessionId,
                    "조금 더 자세히 알려주세요 🙂",
                    List.of()
            );
        };
    }

    private ChatMessageResponse askReferenceByMissingField(
            String sessionId,
            ChatSession session
    ) {
        MissingField field = session.missingReferenceFields().get(0);

        return switch (field) {
            case REFERENCE_TYPE -> ChatMessageResponse.question(
                    sessionId,
                    "어떤 공간을 꾸미고 싶으신가요?",
                    List.of("거실", "침실", "주방")
            );

            case REFERENCE_SIZE -> ChatMessageResponse.question(
                    sessionId,
                    "공간 크기는 어느 정도인가요?",
                    List.of("소형(10평 이하)", "중형(10~20평)", "잘 모르겠어요")
            );

            case REFERENCE_MOOD -> ChatMessageResponse.question(
                    sessionId,
                    "어떤 느낌의 공간을 원하시나요?",
                    List.of("차분한", "아늑한", "모던한", "무드 추천받기")
            );

            case REFERENCE_STYLE -> ChatMessageResponse.question(
                    sessionId,
                    "선호하는 스타일이 있나요?",
                    List.of("클래식" ,"모던", "내추럴", "스타일 추천받기")
            );

            case REFERENCE_COLOR -> ChatMessageResponse.question(
                    sessionId,
                    "좋아하는 컬러가 있나요?",
                    List.of("밝은 톤","차분한 톤", "상관 없어요")
            );

            case REFERENCE_BUDGET -> ChatMessageResponse.question(
                    sessionId,
                    "예산은 어느 정도 생각하고 있나요?",
                    List.of("가성비 위주", "중간 가격대", "직접 입력")
            );

            default -> ChatMessageResponse.question(
                    sessionId,
                    "조금 더 알려주세요 🙂",
                    List.of()
            );
        };
    }




//    /* =========================
//       Question Builders
//    ========================= */
//    private ChatMessageResponse askNextProductQuestion(String sessionId, ChatSession session) {
//
//        if (session.productTypes() == null || session.productTypes().isEmpty()) {
//            return ChatMessageResponse.question(
//                    sessionId,
//                    "어떤 종류의 제품을 찾고 계신가요?",
//                    List.of("가구", "조명", "패브릭")
//            );
//        }
//
//        if (session.productMinBudget() == null && session.productMaxBudget() == null) {
//            return ChatMessageResponse.question(
//                    sessionId,
//                    "예산은 어느 정도로 생각하고 계신가요?",
//                    List.of("10만원 이하", "30만원 이하", "상관없어요")
//            );
//        }
//
//        return ChatMessageResponse.question(
//                sessionId,
//                "이제 추천해드릴 수 있어요!",
//                List.of("추천해줘")
//        );
//    }
//
//    private ChatMessageResponse askNextReferenceQuestion(String sessionId, ChatSession session) {
//
//        if (session.referenceType() == null) {
//            return ChatMessageResponse.question(
//                    sessionId,
//                    "어떤 공간을 꾸미고 싶으신가요?",
//                    List.of("거실", "침실", "서재")
//            );
//        }
//
//        if (session.referenceMoods() == null || session.referenceMoods().isEmpty()) {
//            return ChatMessageResponse.question(
//                    sessionId,
//                    "어떤 분위기를 원하시나요?",
//                    List.of("차분한", "아늑한", "모던한")
//            );
//        }
//
//        if (session.referenceMinBudget() == null && session.referenceMaxBudget() == null) {
//            return ChatMessageResponse.question(
//                    sessionId,
//                    "예산은 어느 정도 생각하고 계세요?",
//                    List.of("50만원 이하", "100만원 이하", "상관없어요")
//            );
//        }
//
//        return ChatMessageResponse.question(
//                sessionId,
//                "이제 인테리어 추천을 해드릴게요 🙂",
//                List.of("추천해줘")
//        );
//    }

}

