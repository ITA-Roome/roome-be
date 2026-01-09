package com.roome.roome.be.domain.reference.service;

import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.entity.ReferenceImage;
import com.roome.roome.be.domain.reference.repository.ReferenceImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReferenceImageService {

    private final ImageUrlBuilder imageUrlBuilder;
    private final S3Service s3Service;
    private final ReferenceImageRepository referenceImageRepository;

    public void registerReferenceImage(
            Reference reference, MultipartFile image
    ) {
        String objectKey = s3Service.uploadObject(StorageScope.REFERENCE, reference.getId(), image);
        String imageUrl = imageUrlBuilder.build(objectKey);
        ReferenceImage referenceImage = ReferenceImage.builder()
                .reference(reference)
                .objectKey(objectKey)
                .imageUrl(imageUrl)
                .build();

        referenceImageRepository.save(referenceImage);
    }
}
