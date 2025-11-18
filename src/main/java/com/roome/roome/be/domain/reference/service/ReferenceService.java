package com.roome.roome.be.domain.reference.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.reference.dto.response.ReferenceToggleScrapResponse;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.entity.ReferenceImage;
import com.roome.roome.be.domain.reference.repository.ReferenceImageRepository;
import com.roome.roome.be.domain.reference.repository.ReferenceRepository;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserScrapReference;
import com.roome.roome.be.domain.user.repository.UserRepository;
import com.roome.roome.be.domain.user.repository.UserScrapReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final ReferenceImageRepository referenceImageRepository;
    private final UserRepository userRepository;
    private final UserScrapReferenceRepository userScrapReferenceRepository;
    private final S3Service s3Service;

    // 레퍼런스 등록
    public void registerReference(Long userId, List<MultipartFile> images ) {

        if(images == null || images.isEmpty()) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Reference reference = referenceRepository.save(Reference.builder()
                .user(user)
                .likeCount(0)
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

    public ReferenceToggleScrapResponse toggleReferenceScrap(Long referenceId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Reference reference = findReferenceById(referenceId);

        boolean scrapped;

        Optional<UserScrapReference> existing = userScrapReferenceRepository.findByUserAndReference(user,reference);
        if (existing.isEmpty()) {
            UserScrapReference userScrapReference = UserScrapReference.builder()
                    .user(user)
                    .reference(reference)
                    .build();
            userScrapReferenceRepository.save(userScrapReference);
            scrapped = true;
        }
        else {
            userScrapReferenceRepository.delete(existing.get());
            scrapped = false;
        }

        return new ReferenceToggleScrapResponse(scrapped);
    }

    public Reference findReferenceById(Long referenceId) {
        return referenceRepository.findById(referenceId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFERENCE_NOT_FOUND));
    }
}
