package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;

import java.util.List;

public interface UserScrapReferenceCustomRepository {
    List<CommonReferenceInfo> findUserScrappedReferenceListByUserId(Long userId);
}
