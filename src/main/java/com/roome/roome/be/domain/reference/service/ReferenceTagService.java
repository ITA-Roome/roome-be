package com.roome.roome.be.domain.reference.service;

import com.roome.roome.be.domain.product.entity.Tag;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.product.repository.TagRepository;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.entity.ReferenceTag;
import com.roome.roome.be.domain.reference.repository.ReferenceTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceTagService {

    private final ReferenceTagRepository referenceTagRepository;
    private final TagRepository tagRepository;

    public void registerReferenceTag(Reference reference, String mood) {

        tagRepository.findByNameAndType(mood, TagType.REFERENCE_MOOD)
                .ifPresentOrElse(
                        // 이미 태그가 존재하는 경우
                        tag -> referenceTagRepository.save(
                                ReferenceTag.builder()
                                        .reference(reference)
                                        .tag(tag)
                                        .build()
                        ),

                        // 태그가 없는 경우
                        () -> {
                            Tag tag = tagRepository.save(
                                    Tag.builder()
                                            .type(TagType.REFERENCE_MOOD)
                                            .name(mood)
                                            .build()
                            );

                            referenceTagRepository.save(
                                    ReferenceTag.builder()
                                            .reference(reference)
                                            .tag(tag)
                                            .build()
                            );
                        }
                );
    }

}
