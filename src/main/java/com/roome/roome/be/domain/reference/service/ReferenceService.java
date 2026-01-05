package com.roome.roome.be.domain.reference.service;

import java.util.*;

import com.roome.roome.be.domain.product.dto.response.ProductTagInfo;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductImage;
import com.roome.roome.be.domain.reference.dto.response.*;
import com.roome.roome.be.domain.reference.repository.ReferenceCustomRepository;
import com.roome.roome.be.domain.user.repository.UserLikeReferenceRepository;
import com.roome.roome.be.domain.user.repository.UserScrapReferenceRepository;
import jakarta.transaction.Transactional;
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

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final UserRepository userRepository;
    private final ReferenceCustomRepository referenceCustomRepository;
    private final UserLikeReferenceRepository userLikeReferenceRepository;
    private final UserScrapReferenceRepository userScrapReferenceRepository; // 스크랩용 레포지토리 주입

    private final S3Service s3Service;
    private final ImageUrlBuilder imageUrlBuilder;


    // 레퍼런스 등록
    public void registerReference(Long userId, List<MultipartFile> images) {

        if (images == null || images.isEmpty()) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Reference reference = referenceRepository.save(Reference.builder()
                .user(user)
                .scrapCount(0)
                .build());
        Long referenceId = reference.getId();

        List<ReferenceImage> referenceImageList = images.stream()
                .map(file -> {
                    String objectKey = s3Service.uploadObject(StorageScope.REFERENCE, referenceId, file);
                    return ReferenceImage.builder()
                            .reference(reference)
                            .objectKey(objectKey) // 컬럼명 object_key 추천
                            .build();
                })
                .toList();
        referenceImageRepository.saveAll(referenceImageList);
    }

    public ReferenceListResponse getReferenceList(Long userId) {
        // todo => 우선 유저만 불러온 뒤, 레퍼런스 관련 기획이 좀 더 상세화되면 그에 맞게 리스트 조회 기능 구체화, 우선은 좋아요 순으로 전체 레퍼런스 정렬
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Reference> referenceList = referenceRepository.findAll();
        List<CommonReferenceInfo> commonReferenceInfoList = referenceList.stream()
                .sorted(Comparator.comparing(Reference::getScrapCount).reversed())
                .map(
                        reference -> {
                            List<String> imageUrlList = reference.getReferenceImageList().stream()
                                    .map(refImg -> imageUrlBuilder.build(
                                            refImg.getObjectKey()
                                    ))
                                    .toList();

                            return new CommonReferenceInfo(
                                    reference.getId(),
                                    reference.getUser().getNickname(),
                                    reference.getUser().getId(),
                                    imageUrlList,
                                    reference.getScrapCount()
                            );
                        }

                ).toList();
        return new ReferenceListResponse(commonReferenceInfoList);
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

        List<String> images = ref.getReferenceImageList().stream()
                .sorted(Comparator.comparing(ReferenceImage::getSortOrder))
                .map(ReferenceImage::getObjectKey)
                .filter(Objects::nonNull)
                .map(imageUrlBuilder::build)
                .toList();

        List<ReferenceItemProductInfo> items = ref.getReferenceItemList().stream()
                .map(com.roome.roome.be.domain.reference.entity.ReferenceItem::getProduct)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Product::getId,
                        product -> product,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(p -> {

                    String thumb = null;

                    if (p.getThumbnailKey() != null && !p.getThumbnailKey().isBlank()) {
                        thumb = imageUrlBuilder.build(p.getThumbnailKey());
                    } else if (!p.getProductImageList().isEmpty()) {
                        thumb = p.getProductImageList().stream()
                                .sorted(Comparator.comparingInt(ProductImage::getSortOrder))
                                .findFirst()
                                .map(ProductImage::getImageUrl)
                                .orElse(null);
                    }

                    Set<ProductTagInfo> tags = p.getProductTagList().stream()
                            .map(pt -> new ProductTagInfo(
                                    pt.getTag().getId(),
                                    pt.getTag().getName(),
                                    pt.getTag().getType()
                            ))
                            .collect(Collectors.toSet());

                    return new ReferenceItemProductInfo(
                            p.getId(),
                            p.getName(),
                            p.getPrice(),
                            p.getProductUrl(),
                            thumb,
                            tags
                    );
                })
                .toList();

        boolean isLiked = false;
        boolean isScrapped = false;

        if (userId != null) {
            isLiked = userLikeReferenceRepository.existsByUserIdAndReferenceId(userId, referenceId);
            isScrapped = userScrapReferenceRepository.existsByUserIdAndReferenceId(userId, referenceId);
        }

        String userProfileUrl = (ref.getUser().getProfileImage() != null)
                ? imageUrlBuilder.build(ref.getUser().getProfileImage())
                : null;

        return new ReferenceDetailResponse(
                ref.getId(),
                ref.getName(),
                ref.getDescription(),
                images,
                items,
                ref.getScrapCount(),
                ref.getLikeCount(),
                isScrapped,
                isLiked,
                ref.getUser().getNickname(),
                userProfileUrl,
                ref.getReferenceUrl()
        );
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
                .map(ref -> {
                    String thumbnail = ref.getReferenceImageList().isEmpty()
                        ? null
                        : imageUrlBuilder.build(ref.getReferenceImageList().get(0).getObjectKey());

                    return new RelatedReferenceResponse(
                            ref.getId(),
                            thumbnail,
                            ref.getScrapCount(),
                            ref.getUser().getNickname(),
                            matchCountMap.get(ref.getId())
                    );
                })
                .toList();
    }
}
