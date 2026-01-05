package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.common.redis.RedisService;
import com.roome.roome.be.domain.ai.dto.request.AiProductRequest;
import com.roome.roome.be.domain.ai.dto.request.AiReferenceRequest;
import com.roome.roome.be.domain.ai.dto.response.AiProductResponse;
import com.roome.roome.be.domain.ai.dto.response.AiReferenceResponse;
import com.roome.roome.be.domain.ai.service.AiService;
import com.roome.roome.be.domain.chat.dto.request.ChatProductScenarioRequest;
import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.ChatProductScenarioResponse;
import com.roome.roome.be.domain.chat.dto.response.ChatReferenceScenarioResponse;
import com.roome.roome.be.domain.chat.dto.response.ProductSummaryResponse;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.model.ChatSession;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRecommendService {

    private final UserService userService;
    private final AiService aiService;
    private final ProductService productService;
    private final ReferenceService referenceService;
    private final RedisService redisService;


    public ChatReferenceScenarioResponse processChatReferenceScenario(ChatReferenceScenarioRequest request) {
        Long userId = request.userId();
        User user = userService.getUserById(userId);

        // 무드와 공간 크기 매칭
        List<ReferenceCategoryMapping> matchedCategories = Arrays.stream(ReferenceCategoryMapping.values())
                .filter(category -> category.isMatch(request.referenceType(), request.referenceSize()))
                .toList();

        if(matchedCategories.isEmpty()) {
            matchedCategories = List.of(
                    ReferenceCategoryMapping.LIGHTING_LED_BULB,
                    ReferenceCategoryMapping.HOME_DECOR_FRAME_POSTER
            );
        }

        // 선호하는 분위기와 스타일 매칭
        Set<ReferenceMood> referenceMoodList =
                request.referenceMood().stream()
                        .flatMap(mood ->
                                ReferenceMoodMapping
                                        .findMoodsByDescription(mood.name())
                                        .stream()
                        )
                        .collect(Collectors.toSet());

        Set<ReferenceStyle> referenceStyleList =
                request.referenceStyle().stream()
                        .flatMap(style ->
                                ReferenceStyleMapping
                                        .findStylesByDescription(style.name())
                                        .stream()
                        )
                        .collect(Collectors.toSet());

        // 2. DB 필터링
        List<CandidateReferenceInfo> candidateReferenceList =
                referenceService.getCandidateReferenceList(
                        matchedCategories,
                        referenceMoodList,
                        referenceStyleList,
                        request.minBudget(),
                        request.maxBudget()
                );

        if (candidateReferenceList.isEmpty()) {
            // 정책에 따라 빈 카드 or 예외 처리 선택
            return ChatReferenceScenarioResponse.empty();
        }

        // AI 요청
        AiReferenceResponse aiResult =
                aiService.recommendReferenceList(
                        AiReferenceRequest.from(user.getNickname(),request, candidateReferenceList)
                );


        // 응답 변환
        ChatReferenceScenarioResponse response = ChatReferenceScenarioResponse.from(aiResult);
//        redisService.save(ChatSession.from(userId, ChatMode.REFERENCE, request, response));
        return response;

    }

    public ChatProductScenarioResponse processChatProductScenario(ChatProductScenarioRequest request) {
        Long userId = request.userId();
        // 1. 카테고리 변환
        List<ProductCategory> categories =
                ProductTypeMapper.getProductCategoryList(request.productTypes());

        // 2. 상품 조회 (여기서 필터 끝)
        List<CandidateProductInfo> candidateProductList =
                productService.getCandidateProductList(
                        request.maxBudget(),
                        request.minBudget(),
                        request.preferredColors()
                );

        if (candidateProductList.isEmpty()) {
            log.warn("후보 상품 없음");
            return ChatProductScenarioResponse.from(List.of());
        }

        // 3. AI 추천
        List<AiProductResponse> aiResult =
                aiService.recommendProductList(
                        AiProductRequest.from(request, candidateProductList, categories )
                );

        // 4. productId → entity 매핑
        Map<Long, CandidateProductInfo> map =
                candidateProductList.stream()
                        .collect(Collectors.toMap(
                                CandidateProductInfo::productId,
                                Function.identity()
                        ));

        // 5. 응답 조립
        List<ProductSummaryResponse> result =
                aiResult.stream()
                        .map(ai -> {
                            CandidateProductInfo p = map.get(ai.productId());
                            if (p == null) return null;

                            return ProductSummaryResponse.from(p, ai);
                        })
                        .filter(Objects::nonNull)
                        .toList();

        ChatProductScenarioResponse response = ChatProductScenarioResponse.from(result);
//        redisService.save(ChatSession.from(userId,ChatMode.PRODUCT, request,response));
        return response;
    }
}
