package com.roome.roome.be.domain.reference.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceListResponse;
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

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final UserRepository userRepository;

    private final S3Service s3Service;
    private final ImageUrlBuilder imageUrlBuilder;


    // 레퍼런스 등록
    public void registerReference(Long userId, List<MultipartFile> images ) {

        if(images == null || images.isEmpty()) return;

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
        return referenceRepository.findCandidateReferenceList(matchedCategories,moodList,styleList,minBudget,maxBudget);
    }
}
