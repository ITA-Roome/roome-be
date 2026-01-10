package com.roome.roome.be.domain.reference.service;

import java.util.*;
import java.util.stream.Collectors;

import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.domain.reference.dto.response.*;
import com.roome.roome.be.domain.reference.mapper.ReferenceMapper;
import com.roome.roome.be.domain.reference.repository.ReferenceCustomRepository;
import com.roome.roome.be.domain.user.dto.response.UserUploadedReferenceListResponse;
import com.roome.roome.be.domain.user.entity.UserOnboarding;
import com.roome.roome.be.domain.user.enums.MoodType;
import com.roome.roome.be.domain.user.enums.SpaceType;
import com.roome.roome.be.domain.user.repository.UserLikeReferenceRepository;
import com.roome.roome.be.domain.user.repository.UserOnboardingRepository;
import com.roome.roome.be.domain.user.repository.UserScrapReferenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.enums.ReferenceCategoryMapping;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
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
    private final ImageUrlBuilder imageUrlBuilder;

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

        // keyWord 있으면 검색만
        Sort bucketSort = Sort.by(Sort.Direction.DESC, "likeCount")
            .and(Sort.by(Sort.Direction.DESC, "id"));

        Pageable bucketPageable = org.springframework.data.domain.PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            bucketSort
        );

        if (keyWord != null && !keyWord.isBlank()) {
            Page<Reference> page = referenceCustomRepository.findBaseList(keyWord, bucketPageable);
            if (page.isEmpty()) return Page.empty(bucketPageable);
            return mapToCommonInfo(page, userId);
        }

        // keyWord 없을 때만: 추천 + 비추천 섞기
        int pageSize = bucketPageable.getPageSize();
        long offset = bucketPageable.getOffset();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        UserOnboarding onboarding = userOnboardingRepository.findByUser(user).orElse(null);

        List<MoodType> moodTypes = (onboarding != null && onboarding.getMoodType() != null)
            ? List.of(onboarding.getMoodType()) : List.of();

        List<SpaceType> spaceTypes = (onboarding != null && onboarding.getSpaceType() != null)
            ? List.of(onboarding.getSpaceType()) : List.of();

        // 온보딩 없으면 전체
        if (moodTypes.isEmpty() && spaceTypes.isEmpty()) {
            Page<Reference> page = referenceCustomRepository.findBaseList(null, bucketPageable);
            if (page.isEmpty()) return Page.empty(bucketPageable);
            return mapToCommonInfo(page, userId);
        }

        // 전체 갯수
        long totalAll = referenceCustomRepository.countBase(null);

        // 추천 갯수
        long recTotal = referenceCustomRepository.countRecommendedWithKeyword(null, moodTypes, spaceTypes);


        long recStart = offset;
        int recLimit = (recStart < recTotal)
            ? (int) Math.min(pageSize, recTotal - recStart)
            : 0;

        List<Reference> recommended = (recLimit > 0)
            ? referenceCustomRepository.findRecommendedSliceWithKeyword(
            null, moodTypes, spaceTypes, recStart, recLimit, bucketSort
        )
            : List.of();

        int remain = pageSize - recommended.size();
        if (remain <= 0) {
            return mapToCommonInfo(new org.springframework.data.domain.PageImpl<>(recommended, bucketPageable, totalAll), userId);
        }

        long nonRecOffset = Math.max(0, offset - recTotal);

        List<Reference> nonRecommended =
            referenceCustomRepository.findNonRecommendedSliceWithKeyword(
                null, moodTypes, spaceTypes, nonRecOffset, remain, bucketSort
            );

        List<Reference> mixed = new ArrayList<>(pageSize);
        mixed.addAll(recommended);
        mixed.addAll(nonRecommended);

        if (mixed.isEmpty()) return Page.empty(bucketPageable);
        return mapToCommonInfo(mixed, bucketPageable, totalAll, userId);
    }

    private Page<CommonReferenceInfo> mapToCommonInfo(Page<Reference> page, Long userId) {
        return mapToCommonInfo(page.getContent(), page.getPageable(), page.getTotalElements(), userId);
    }

    private Page<CommonReferenceInfo> mapToCommonInfo(List<Reference> references, Pageable pageable, long total, Long userId) {
        List<Long> referenceIds = references.stream().map(Reference::getId).toList();

        Set<Long> scrappedIds = (userId != null && !referenceIds.isEmpty())
            ? userScrapReferenceRepository.findScrappedReferenceIds(userId, referenceIds)
            : new HashSet<>();

        Set<Long> likedIds = (userId != null && !referenceIds.isEmpty())
            ? userLikeReferenceRepository.findLikedReferenceIds(userId, referenceIds)
            : new HashSet<>();

        List<CommonReferenceInfo> content = references.stream()
            .map(ref -> referenceMapper.toCommonInfo(
                ref,
                scrappedIds.contains(ref.getId()),
                likedIds.contains(ref.getId())
            ))
            .toList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
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

    public UserUploadedReferenceListResponse getUserUploadedReferenceList(Long userId) {
        List<CommonReferenceInfo> rawList = referenceCustomRepository
                .findUserUploadedReferenceListByUserId(userId);

        List<Long> referenceIds = rawList.stream()
                .map(CommonReferenceInfo::referenceId)
                .toList();

        Set<Long> likedIds = new HashSet<>();
        Set<Long> scrappedIds = new HashSet<>();

        if (!referenceIds.isEmpty()) {
            likedIds = userLikeReferenceRepository.findLikedReferenceIds(userId, referenceIds);
            scrappedIds = userScrapReferenceRepository.findScrappedReferenceIds(userId, referenceIds);
        }

        final Set<Long> finalLikedIds = likedIds;
        final Set<Long> finalScrappedIds = scrappedIds;

        List<CommonReferenceInfo> finalList = rawList.stream()
                .map(raw -> new CommonReferenceInfo(
                        raw.referenceId(),
                        raw.nickname(),
                        raw.userId(),
                        raw.imageUrlList().stream()
                                .map(imageUrlBuilder::build)
                                .toList(),
                        raw.scrapCount(),
                        raw.likeCount(),
                        finalScrappedIds.contains(raw.referenceId()),
                        finalLikedIds.contains(raw.referenceId())
                ))
                .toList();

        return new UserUploadedReferenceListResponse(finalList);
    }
}