package com.roome.roome.be.domain.user.repository;

import java.util.List;

import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;

public interface UserLikeReferenceCustomRepository {
    List<CommonReferenceInfo> findUserLikeReferenceListByUserId(Long userId);
}
