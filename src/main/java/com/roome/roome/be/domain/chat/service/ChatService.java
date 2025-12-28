package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.ai.dto.request.AiProductRequest;
import com.roome.roome.be.domain.ai.dto.response.AiProductResponse;
import com.roome.roome.be.domain.ai.service.AiService;
import com.roome.roome.be.domain.chat.dto.request.ChatScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.ChatScenarioResponse;
import com.roome.roome.be.domain.chat.dto.response.ProductSummaryResponse;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductTypeMapper;
import com.roome.roome.be.domain.product.service.ProductService;
import com.roome.roome.be.domain.reference.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ProductService productService;
    private final AiService aiService;

    public ChatScenarioResponse processChatScenario(Long userId, ChatScenarioRequest request) {
        return switch (request.chatMode()) {
            case PRODUCT -> processProductScenario(request);
            case REFERENCE -> processChatReferenceScenario(request);
        };
    }

    private ChatScenarioResponse processChatReferenceScenario(ChatScenarioRequest request) {

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
        Set<ReferenceMood> referenceMoodList= ReferenceMoodMapping.findMoodsByDescription(request.referenceMood().name());
        Set<ReferenceStyle> referenceStyleList= ReferenceStyleMapping.findStylesByDescription(request.referenceStyle().name());

        return null;
    }

    private ChatScenarioResponse processProductScenario(ChatScenarioRequest request) {

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
            return ChatScenarioResponse.from(List.of());
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

        return ChatScenarioResponse.from(result);
    }
}

