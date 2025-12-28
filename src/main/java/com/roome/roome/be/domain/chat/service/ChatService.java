package com.roome.roome.be.domain.chat.service;

import com.roome.roome.be.domain.ai.dto.request.AiProductRequest;
import com.roome.roome.be.domain.ai.dto.response.AiProductResponse;
import com.roome.roome.be.domain.ai.service.AiService;
import com.roome.roome.be.domain.chat.dto.request.ChatScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.ChatScenarioResponse;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductTypeMapper;
import com.roome.roome.be.domain.product.service.ProductService;
import com.roome.roome.be.domain.reference.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ProductService productService;
    private final AiService aiService;


    public ChatScenarioResponse processChatScenario(Long userId, ChatScenarioRequest request) {
        return switch (request.chatMode()) {
            case REFERENCE -> processChatReferenceScenario(request);
            case PRODUCT -> processChatProductScenario(request);
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

    private ChatScenarioResponse processChatProductScenario(ChatScenarioRequest request) {
        // 사용자 입력값에 맞는 제품 유형 ENUM값 가져오기
        List<ProductCategory> categoryList = ProductTypeMapper.getProductCategoryList(request.productTypes());
        List<CandidateProductInfo> candidateProductInfoList = productService.getCandidateProductList(categoryList,request.maxBudget(), request.minBudget());

        // 3. AI 추천 실행
        List<AiProductResponse> aiResult =
                aiService.recommendProductList(AiProductRequest.from(request, candidateProductInfoList));

        return ChatScenarioResponse.from(aiResult);
    }
}
