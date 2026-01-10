package com.roome.roome.be.domain.reference.repository;

import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.enums.ReferenceCategoryMapping;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import com.roome.roome.be.domain.user.enums.MoodType;
import com.roome.roome.be.domain.user.enums.SpaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public interface ReferenceCustomRepository {
    List<CandidateReferenceInfo> findCandidateReferenceList(
            List<ReferenceCategoryMapping> matchedCategories,
            Set<ReferenceMood> moodList,
            Set<ReferenceStyle> styleList,
            Integer minBudget,
            Integer maxBudget
    );

    List<ReferenceMatchResult> findRelatedReferencesByProductTags(
            Long productId,
            int minMatchCount,
            int limit
    );

    Page<Reference> findRecommendedList(List<MoodType> moodTypes, List<SpaceType> spaceTypes, Pageable pageable);

    record ReferenceMatchResult(
            Long referenceId,
            Integer matchedTagCount
    ) {
    }

    List<CommonReferenceInfo> findUserUploadedReferenceListByUserId(Long userId);

    Page<Reference> findBaseList(String keyWord, Pageable pageable);

    long countBase(String keyWord);

    // 추천 count
    long countRecommendedWithKeyword(String keyWord, List<MoodType> moodTypes, List<SpaceType> spaceTypes);

    // 추천
    List<Reference> findRecommendedSliceWithKeyword(
        String keyWord,
        List<MoodType> moodTypes,
        List<SpaceType> spaceTypes,
        long offset,
        int limit,
        Sort sort
    );

    // 비추천 slice (keyWord 포함, NOT 추천)
    List<Reference> findNonRecommendedSliceWithKeyword(
        String keyWord,
        List<MoodType> moodTypes,
        List<SpaceType> spaceTypes,
        long offset,
        int limit,
        Sort sort
    );
}
