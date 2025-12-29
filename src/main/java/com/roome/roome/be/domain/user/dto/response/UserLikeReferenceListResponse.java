package com.roome.roome.be.domain.user.dto.response;

import java.util.List;

import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;

public record UserLikeReferenceListResponse(
	List<CommonReferenceInfo> userLikeReferenceList
) {
}
