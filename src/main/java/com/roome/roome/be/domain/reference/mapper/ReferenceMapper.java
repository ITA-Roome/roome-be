package com.roome.roome.be.domain.reference.mapper;

import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.domain.product.dto.response.ProductTagInfo;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductImage;
import com.roome.roome.be.domain.reference.dto.response.*;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.entity.ReferenceImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReferenceMapper {

    private final ImageUrlBuilder imageUrlBuilder;

    // 목록 조회용 DTO 변환
    public CommonReferenceInfo toCommonInfo(Reference reference, boolean isScrapped, boolean isLiked) {
        List<String> imageUrlList = reference.getReferenceImageList().stream()
                .sorted(Comparator.comparing(ReferenceImage::getSortOrder))
                .map(refImg -> imageUrlBuilder.build(refImg.getObjectKey()))
                .toList();

        return new CommonReferenceInfo(
                reference.getId(),
                reference.getUser().getNickname(),
                reference.getUser().getId(),
                imageUrlList,
                reference.getScrapCount(),
                reference.getLikeCount(),
                isScrapped,
                isLiked
        );
    }

    // 상세 조회용 DTO 변환
    public ReferenceDetailResponse toDetailResponse(Reference ref, boolean isScrapped, boolean isLiked) {
        List<String> images = ref.getReferenceImageList().stream()
                .sorted(Comparator.comparing(ReferenceImage::getSortOrder))
                .map(ReferenceImage::getObjectKey)
                .filter(Objects::nonNull)
                .map(imageUrlBuilder::build)
                .toList();

        List<ReferenceItemProductInfo> items = ref.getReferenceItemList().stream()
                .map(com.roome.roome.be.domain.reference.entity.ReferenceItem::getProduct)
                .filter(Objects::nonNull)
                .distinct()
                .map(this::toProductInfo)
                .toList();

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
                ref.getUser().getId(),
                userProfileUrl,
                ref.getReferenceUrl()
        );
    }

    public RelatedReferenceResponse toRelatedResponse(Reference ref, int matchedCount) {
        String thumbnail = ref.getReferenceImageList().isEmpty()
                ? null
                : imageUrlBuilder.build(ref.getReferenceImageList().get(0).getObjectKey());

        return new RelatedReferenceResponse(
                ref.getId(),
                thumbnail,
                ref.getScrapCount(),
                ref.getUser().getNickname(),
                ref.getUser().getId(),
                matchedCount
        );
    }

    //  상품 정보 변환 로직
    private ReferenceItemProductInfo toProductInfo(Product p) {
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

        var tags = p.getProductTagList().stream()
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
    }
}