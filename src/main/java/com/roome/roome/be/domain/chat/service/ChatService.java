package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.common.redis.RedisService;
import com.roome.roome.be.domain.ai.dto.request.AiProductRequest;
import com.roome.roome.be.domain.ai.dto.request.AiReferenceRequest;
import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.ai.dto.response.AiProductResponse;
import com.roome.roome.be.domain.ai.dto.response.AiReferenceResponse;
import com.roome.roome.be.domain.ai.service.AiIntentService;
import com.roome.roome.be.domain.ai.service.AiService;
import com.roome.roome.be.domain.chat.dto.request.ChatMessageRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatProductScenarioRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.*;
import com.roome.roome.be.domain.chat.enums.ChatInputType;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.model.ChatSession;
import com.roome.roome.be.domain.chat.repository.ChatSessionRepository;
import com.roome.roome.be.domain.chat.repository.InMemoryChatSessionRepository;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductTypeMapper;
import com.roome.roome.be.domain.product.service.ProductService;
import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.enums.*;
import com.roome.roome.be.domain.reference.service.ReferenceService;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ProductService productService;
    private final ReferenceService referenceService;
    private final UserService userService;
    private final AiService aiService;
    private final AiIntentService aiIntentService;
    private final RedisService redisService;
    private final ChatSessionService chatSessionService;

    private final ChatSessionRepository chatSessionRepository;
    private final InMemoryChatSessionRepository inMemoryChatSessionRepository; // newSessionId용(추후 factory로 분리 추천)


    public ChatMessageResponse handle(Long userId, ChatMessageRequest request) {
        return chatSessionService.handle(
                userId,
                request.sessionId(),
                request.inputType(),
                request.message()
        );
    }



//    public ChatReferenceScenarioResponse processChatReferenceScenario(Long userId, ChatReferenceScenarioRequest request) {
//        User user = userService.getUserById(userId);
//
//        // 무드와 공간 크기 매칭
//        List<ReferenceCategoryMapping> matchedCategories = Arrays.stream(ReferenceCategoryMapping.values())
//                .filter(category -> category.isMatch(request.referenceType(), request.referenceSize()))
//                .toList();
//
//        if(matchedCategories.isEmpty()) {
//            matchedCategories = List.of(
//                    ReferenceCategoryMapping.LIGHTING_LED_BULB,
//                    ReferenceCategoryMapping.HOME_DECOR_FRAME_POSTER
//            );
//        }
//
//        // 선호하는 분위기와 스타일 매칭
//        Set<ReferenceMood> referenceMoodList= ReferenceMoodMapping.findMoodsByDescription(request.referenceMood().name());
//        Set<ReferenceStyle> referenceStyleList= ReferenceStyleMapping.findStylesByDescription(request.referenceStyle().name());
//
//        // 2. DB 필터링
//        List<CandidateReferenceInfo> candidateReferenceList =
//                referenceService.getCandidateReferenceList(
//                        matchedCategories,
//                        referenceMoodList,
//                        referenceStyleList,
//                        request.minBudget(),
//                        request.maxBudget()
//                );
//
//        if (candidateReferenceList.isEmpty()) {
//            // 정책에 따라 빈 카드 or 예외 처리 선택
//            return ChatReferenceScenarioResponse.empty();
//        }
//
//        // AI 요청
//        AiReferenceResponse aiResult =
//                aiService.recommendReferenceList(
//                        AiReferenceRequest.from(user.getNickname(),request, candidateReferenceList)
//                );
//
//
//        // 응답 변환
//        ChatReferenceScenarioResponse response = ChatReferenceScenarioResponse.from(aiResult);
//        redisService.save(ChatSession.from(userId, ChatMode.REFERENCE, request, response));
//        return response;
//
//    }
//
//    public ChatProductScenarioResponse processChatProductScenario(Long userId, ChatProductScenarioRequest request) {
//
//        // 1. 카테고리 변환
//        List<ProductCategory> categories =
//                ProductTypeMapper.getProductCategoryList(request.productTypes());
//
//        // 2. 상품 조회 (여기서 필터 끝)
//        List<CandidateProductInfo> candidateProductList =
//                productService.getCandidateProductList(
//                        request.maxBudget(),
//                        request.minBudget(),
//                        request.preferredColors()
//                );
//
//        if (candidateProductList.isEmpty()) {
//            log.warn("후보 상품 없음");
//            return ChatProductScenarioResponse.from(List.of());
//        }
//
//        // 3. AI 추천
//        List<AiProductResponse> aiResult =
//                aiService.recommendProductList(
//                        AiProductRequest.from(request, candidateProductList, categories )
//                );
//
//        // 4. productId → entity 매핑
//        Map<Long, CandidateProductInfo> map =
//                candidateProductList.stream()
//                        .collect(Collectors.toMap(
//                                CandidateProductInfo::productId,
//                                Function.identity()
//                        ));
//
//        // 5. 응답 조립
//        List<ProductSummaryResponse> result =
//                aiResult.stream()
//                        .map(ai -> {
//                            CandidateProductInfo p = map.get(ai.productId());
//                            if (p == null) return null;
//
//                            return ProductSummaryResponse.from(p, ai);
//                        })
//                        .filter(Objects::nonNull)
//                        .toList();
//
//        ChatProductScenarioResponse response = ChatProductScenarioResponse.from(result);
//        redisService.save(ChatSession.from(userId,ChatMode.PRODUCT, request,response));
//        return response;
//    }
//
//
//    public ChatMessageResponse handle(Long userId, ChatMessageRequest req) {
//
//        // 1) 세션 로드 or 생성
//        ChatSession session = loadOrCreate(userId, req.sessionId());
//        session.touch();
//
//        String message = req.message().trim();
//
//        // 2) reset 트리거
//        if (isReset(message)) {
//            chatSessionRepository.delete(session.getSessionId());
//            ChatSession newSession = ChatSession.create(inMemoryChatSessionRepository.newSessionId(), userId);
//            chatSessionRepository.save(newSession);
//            return ChatMessageResponse.question(
//                    newSession.getSessionId(),
//                    "어떤 서비스를 이용하시겠어요?",
//                    List.of("방 인테리어 추천", "특정 제품 추천")
//            );
//        }
//
//        // 3) 모드 결정(초기 진입)
//        if (session.getMode() == null) {
//            ChatMode mode = detectMode(message, req.inputType());
//            if (mode == null) {
//                // 아직 모드 결정을 못 하면 모드 질문
//                chatSessionRepository.save(session);
//                return ChatMessageResponse.question(
//                        session.getSessionId(),
//                        "어떤 서비스를 이용하시겠어요?",
//                        List.of("방 인테리어 추천", "특정 제품 추천")
//                );
//            }
//            session.setMode(mode);
//            session.setStep(0);
//        }
//
//        // 4) 추천 트리거 감지
//        boolean recommend = isRecommendTrigger(message);
//
//        // 5) 상태 업데이트 (버튼이면 rule 기반, 텍스트면 필요시 AI)
//        if (req.inputType() == ChatInputType.BUTTON) {
//            applyButton(session, message);
//        } else {
//            // 우선 rule 기반으로 최소 처리(예: 예산/색상/키워드)
//            applyTextRule(session, message);
//
//            // 애매하면 나중에: AI Intent Analyzer 붙일 자리
//            // intentAnalyzer.analyzeAndUpdate(session, message);
//        }
//
//        // 6) 추천 or 다음 질문
//        ChatMessageResponse response;
//        if (session.getMode() == ChatMode.PRODUCT) {
//            if (recommend && session.readyToRecommendProduct()) {
//                response = recommendProduct(userId, session);
//            } else {
//                response = nextQuestionForProduct(session);
//            }
//        } else { // REFERENCE
//            if (recommend && session.readyToRecommendReference()) {
//                response = recommendReference(userId, session);
//            } else {
//                response = nextQuestionForReference(session);
//            }
//        }
//
//        // 7) 세션 저장
//        chatSessionRepository.save(session);
//
//        return response;
//    }
//
//    private ChatSession loadOrCreate(Long userId, String sessionId) {
//        if (sessionId == null || sessionId.isBlank()) {
//            ChatSession s = ChatSession.create(inMemoryChatSessionRepository.newSessionId(), userId);
//            chatSessionRepository.save(s);
//            return s;
//        }
//        return chatSessionRepository.findById(sessionId)
//                .orElseGet(() -> {
//                    ChatSession s = ChatSession.create(sessionId, userId);
//                    chatSessionRepository.save(s);
//                    return s;
//                });
//    }
//
//    // ===== 트리거/모드 감지 =====
//
//    private boolean isReset(String msg) {
//        return msg.contains("다시") || msg.contains("처음") || msg.contains("초기화");
//    }
//
//    private boolean isRecommendTrigger(String msg) {
//        return msg.contains("추천") || msg.contains("보여") || msg.contains("골라") || msg.contains("결과");
//    }
//
//    private ChatMode detectMode(String msg, ChatInputType type) {
//        // 버튼 문구를 그대로 message로 받는 전제
//        if (msg.contains("인테리어") || msg.contains("방") || msg.contains("무드") || msg.contains("레퍼런스")) {
//            return ChatMode.REFERENCE;
//        }
//        if (msg.contains("제품") || msg.contains("가구") || msg.contains("조명") || msg.contains("커튼")) {
//            return ChatMode.PRODUCT;
//        }
//        return null;
//    }
//
//    // ===== 상태 업데이트(최소 rule) =====
//
//    private void applyButton(ChatSession session, String msg) {
//        // 여기서 버튼 문구를 기준으로 step별 처리(간단 버전)
//        // TODO: step 기반으로 더 정교하게 매핑
//        if (session.getMode() == ChatMode.PRODUCT) {
//            // 예: "조명" "가구" 같은 버튼이 온다면 productTypes 업데이트
//            // 실제 ProductType enum 매핑은 프로젝트 규칙에 맞게 구현
//        } else {
//            // referenceType/size/mood/style 등 업데이트
//        }
//    }
//
//    private void applyTextRule(ChatSession session, String msg) {
//        // 예산 간단 파싱 예시(“20만”, “200000” 등)
//        Integer money = parseBudget(msg);
//        if (money != null) {
//            session.setMaxBudget(money);
//        }
//
//        // 색상 키워드 예시
//        if (msg.contains("화이트")) session.getPreferredColors().add("WHITE");
//        if (msg.contains("베이지")) session.getPreferredColors().add("BEIGE");
//        if (msg.contains("블랙")) session.getPreferredColors().add("BLACK");
//    }
//
//    private Integer parseBudget(String msg) {
//        // 매우 단순 버전: 숫자만 추출(운영 전 개선 필요)
//        String digits = msg.replaceAll("[^0-9]", "");
//        if (digits.isBlank()) return null;
//        try {
//            int v = Integer.parseInt(digits);
//            // “20만”처럼 만 단위 처리 하고 싶으면 여기서 추가 규칙
//            return v;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    // ===== 다음 질문 생성(우선 간단 버전) =====
//
//    private ChatMessageResponse nextQuestionForProduct(ChatSession session) {
//        // slot이 비어 있는 것부터 질문
//        if (session.getProductTypes().isEmpty()) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "어떤 종류의 제품을 찾고 계신가요?",
//                    List.of("가구", "조명", "패브릭 & 데코", "커튼 & 블라인드"));
//        }
//        if (session.getMaxBudget() == null && session.getMinBudget() == null) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "가격대는 어느 정도면 좋을까요?",
//                    List.of("가성비", "중간 가격대", "상관없어요"));
//        }
//        if (session.getPreferredColors().isEmpty()) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "컬러나 분위기 취향이 있을까요?",
//                    List.of("밝은 계열", "어두운 계열", "상관없어요"));
//        }
//        // 조건이 어느 정도 있으면 추천 유도
//        return ChatMessageResponse.question(session.getSessionId(),
//                "지금 조건으로 추천해드릴까요?",
//                List.of("추천해줘", "조건 더 말할래", "처음부터 다시"));
//    }
//
//    private ChatMessageResponse nextQuestionForReference(ChatSession session) {
//        if (session.getReferenceType() == null) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "어떤 공간을 꾸미고 싶으신가요?",
//                    List.of("침실", "거실", "기타 공간"));
//        }
//        if (session.getReferenceSize() == null) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "공간 크기는 어느 정도인가요?",
//                    List.of("소형(10평 이하)", "중형(10평 이상)", "잘 모르겠어요"));
//        }
//        if (session.getReferenceMood() == null) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "어떤 분위기의 공간을 원하시나요?",
//                    List.of("차분한", "아늑한", "모던한", "잘 모르겠어요"));
//        }
//        if (session.getReferenceStyle() == null) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "선호하는 스타일이나 참고 키워드가 있나요?",
//                    List.of("클래식", "모던", "네추럴", "추천해주세요"));
//        }
//        if (session.getMaxBudget() == null && session.getMinBudget() == null) {
//            return ChatMessageResponse.question(session.getSessionId(),
//                    "예산은 어느 정도로 생각하고 계신가요?",
//                    List.of("가성비 위주", "중간 가격대", "직접 입력"));
//        }
//        return ChatMessageResponse.question(session.getSessionId(),
//                "지금 조건으로 추천해드릴까요?",
//                List.of("추천해줘", "더 수정할래", "처음부터 다시"));
//    }
//
//    // ===== 추천 실행(기존 코드 재사용) =====
//
//    private ChatMessageResponse recommendProduct(Long userId, ChatSession session) {
//
//        // 기존: request DTO 기반 → 세션 기반으로 바꿈
//        List<ProductCategory> categories =
//                ProductTypeMapper.getProductCategoryList(session.getProductTypes());
//
//        List<CandidateProductInfo> candidates =
//                productService.getCandidateProductList(
//                        session.getMaxBudget(),
//                        session.getMinBudget(),
//                        session.getPreferredColors()
//                );
//
//        if (candidates.isEmpty()) {
//            return ChatMessageResponse.result(
//                    session.getSessionId(),
//                    "조건에 맞는 상품을 찾지 못했어요. 조건을 조금 바꿔볼까요?",
//                    List.of("조건 다시 입력", "처음부터 다시"),
//                    ChatProductScenarioResponse.from(List.of())
//            );
//        }
//
//        List<AiProductResponse> aiResult =
//                aiService.recommendProductList(
//                        AiProductRequest.from(/*TODO: 세션 기반 from() 새로 만들기*/ null, candidates, categories)
//                );
//
//        Map<Long, CandidateProductInfo> map =
//                candidates.stream().collect(Collectors.toMap(CandidateProductInfo::productId, Function.identity()));
//
//        List<ProductSummaryResponse> result =
//                aiResult.stream()
//                        .map(ai -> {
//                            CandidateProductInfo p = map.get(ai.productId());
//                            return (p == null) ? null : ProductSummaryResponse.from(p, ai);
//                        })
//                        .filter(Objects::nonNull)
//                        .toList();
//
//        return ChatMessageResponse.result(
//                session.getSessionId(),
//                "추천 결과를 보여드릴게요!",
//                List.of("더 추천해줘", "더 저렴한 건?", "처음부터 다시"),
//                ChatProductScenarioResponse.from(result)
//        );
//    }
//
//    private ChatMessageResponse recommendReference(Long userId, ChatSession session) {
//        User user = userService.getUserById(userId);
//
//        // 기존 로직 그대로 재사용(세션 -> request equivalent)
//        List<ReferenceCategoryMapping> matchedCategories = Arrays.stream(ReferenceCategoryMapping.values())
//                .filter(category -> category.isMatch(session.getReferenceType(), session.getReferenceSize()))
//                .toList();
//
//        if (matchedCategories.isEmpty()) {
//            matchedCategories = List.of(
//                    ReferenceCategoryMapping.LIGHTING_LED_BULB,
//                    ReferenceCategoryMapping.HOME_DECOR_FRAME_POSTER
//            );
//        }
//
//        Set<ReferenceMood> moods = (session.getReferenceMood() == null)
//                ? Set.of()
//                : ReferenceMoodMapping.findMoodsByDescription(session.getReferenceMood().name());
//
//        Set<ReferenceStyle> styles = (session.getReferenceStyle() == null)
//                ? Set.of()
//                : ReferenceStyleMapping.findStylesByDescription(session.getReferenceStyle().name());
//
//        List<CandidateReferenceInfo> candidates =
//                referenceService.getCandidateReferenceList(
//                        matchedCategories,
//                        moods,
//                        styles,
//                        session.getMinBudget(),
//                        session.getMaxBudget()
//                );
//
//        if (candidates.isEmpty()) {
//            return ChatMessageResponse.result(
//                    session.getSessionId(),
//                    "조건에 맞는 레퍼런스를 찾지 못했어요. 조건을 조금 바꿔볼까요?",
//                    List.of("조건 다시 입력", "처음부터 다시"),
//                    ChatReferenceScenarioResponse.empty()
//            );
//        }
//
//        AiReferenceResponse ai =
//                aiService.recommendReferenceList(
//                        AiReferenceRequest.from(user.getNickname(), /*TODO: 세션 기반 from() 새로 만들기*/ null, candidates)
//                );
//
//        return ChatMessageResponse.result(
//                session.getSessionId(),
//                "인테리어 무드 추천 결과예요!",
//                List.of("활용 가능한 제품 추천해줘", "더 추천해줘", "처음부터 다시"),
//                ChatReferenceScenarioResponse.from(ai)
//        );
//    }
}

