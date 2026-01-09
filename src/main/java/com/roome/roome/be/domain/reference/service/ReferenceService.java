package com.roome.roome.be.domain.reference.service;

import java.util.*;
import java.util.stream.Collectors;

import com.roome.roome.be.domain.product.dto.response.ProductTagInfo;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductImage;
import com.roome.roome.be.domain.reference.dto.request.RegisterReferenceRequest;
import com.roome.roome.be.domain.reference.dto.response.*;
import com.roome.roome.be.domain.reference.mapper.ReferenceMapper;
import com.roome.roome.be.domain.reference.repository.ReferenceCustomRepository;
import com.roome.roome.be.domain.user.entity.UserOnboarding;
import com.roome.roome.be.domain.user.enums.MoodType;
import com.roome.roome.be.domain.user.enums.SpaceType;
import com.roome.roome.be.domain.user.repository.UserLikeReferenceRepository;
import com.roome.roome.be.domain.user.repository.UserOnboardingRepository;
import com.roome.roome.be.domain.user.repository.UserScrapReferenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.entity.ReferenceImage;
import com.roome.roome.be.domain.reference.enums.ReferenceCategoryMapping;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import com.roome.roome.be.domain.reference.repository.ReferenceImageRepository;
import com.roome.roome.be.domain.reference.repository.ReferenceRepository;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final UserRepository userRepository;
    private final ReferenceCustomRepository referenceCustomRepository;
    private final UserLikeReferenceRepository userLikeReferenceRepository;
    private final UserScrapReferenceRepository userScrapReferenceRepository;
    private final UserOnboardingRepository userOnboardingRepository;

    private final ReferenceTagService referenceTagService;
    private final ReferenceImageService referenceImageService;
    private final ReferenceMapper referenceMapper;

    // 레퍼런스 등록
    @Transactional
    public void registerReference(Long userId, MultipartFile image, String name, String description, String mood) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Reference reference = referenceRepository.save(
                Reference.builder()
                        .user(user)
                        .name(name)
                        .description(description)
                        .scrapCount(0)
                        .likeCount(0)
                        .build());

        referenceImageService.registerReferenceImage(reference, image);
        referenceTagService.registerReferenceTag(reference, mood);
    }

    // 레퍼런스 목록 조회
    @Transactional
    public Page<CommonReferenceInfo> getReferenceList(Long userId, String keyWord, Pageable pageable) {

        // 1. 조회 대상 결정 (검색 vs 추천)
        Page<Reference> referencePage;
        if (keyWord != null && !keyWord.isBlank()) {
            referencePage = referenceRepository.findByNameContaining(keyWord, pageable);
        } else {
            referencePage = getRecommendedReferences(userId, pageable);
        }

        if (referencePage.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. 좋아요/스크랩 정보 조회
        List<Long> referenceIds = referencePage.getContent().stream().map(Reference::getId).toList();
        Set<Long> scrappedIds = (userId != null) ? userScrapReferenceRepository.findScrappedReferenceIds(userId, referenceIds) : new HashSet<>();
        Set<Long> likedIds = (userId != null) ? userLikeReferenceRepository.findLikedReferenceIds(userId, referenceIds) : new HashSet<>();

        // 3. 변환
        return referencePage.map(ref ->
                referenceMapper.toCommonInfo(ref, scrappedIds.contains(ref.getId()), likedIds.contains(ref.getId()))
        );
    }

    private Page<Reference> getRecommendedReferences(Long userId, Pageable pageable) {
        if (userId == null) return referenceRepository.findAll(pageable); // 비로그인 대비(혹은 예외)

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        UserOnboarding onboarding = userOnboardingRepository.findByUser(user).orElse(null);

        List<MoodType> moodTypes = (onboarding != null && onboarding.getMoodType() != null)
                ? List.of(onboarding.getMoodType()) : Collections.emptyList();
        List<SpaceType> spaceTypes = (onboarding != null && onboarding.getSpaceType() != null)
                ? List.of(onboarding.getSpaceType()) : Collections.emptyList();

        return referenceCustomRepository.findRecommendedList(moodTypes, spaceTypes, pageable);
    }

    public Reference findReferenceById(Long referenceId) {
        return referenceRepository.findById(referenceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFERENCE_NOT_FOUND));
    }

    public List<CandidateReferenceInfo> getCandidateReferenceList(
            List<ReferenceCategoryMapping> matchedCategories,
            Set<ReferenceMood> moodList,
            Set<ReferenceStyle> styleList,
            Integer minBudget,
            Integer maxBudget
    ) {
        return referenceCustomRepository.findCandidateReferenceList(matchedCategories, moodList, styleList, minBudget, maxBudget);
    }

    // 레퍼런스(피드) 상세 조회
    public ReferenceDetailResponse getReferenceDetail(Long referenceId, Long userId) {
        Reference ref = referenceRepository.findById(referenceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFERENCE_NOT_FOUND));

        boolean isLiked = (userId != null) && userLikeReferenceRepository.existsByUserIdAndReferenceId(userId, referenceId);
        boolean isScrapped = (userId != null) && userScrapReferenceRepository.existsByUserIdAndReferenceId(userId, referenceId);

        return referenceMapper.toDetailResponse(ref, isScrapped, isLiked);
    }

    // 상품 관련 레퍼런스 조회
    @Transactional
    public List<RelatedReferenceResponse> getRelatedReferences(Long productId, int limit) {
        var matches = referenceCustomRepository.findRelatedReferencesByProductTags(productId, 1, limit);
        if (matches.isEmpty()) return List.of();

        var referenceIds = matches.stream().map(ReferenceCustomRepository.ReferenceMatchResult::referenceId).toList();
        var references = referenceRepository.findAllById(referenceIds);

        var matchCountMap = matches.stream()
                .collect(Collectors.toMap(
                        ReferenceCustomRepository.ReferenceMatchResult::referenceId,
                        ReferenceCustomRepository.ReferenceMatchResult::matchedTagCount
                ));

        return referenceIds.stream()
                .map(id -> references.stream().filter(ref -> ref.getId().equals(id)).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .map(ref -> referenceMapper.toRelatedResponse(ref, matchCountMap.get(ref.getId())))
                .toList();
    }
}