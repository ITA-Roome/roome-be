package com.roome.roome.be.domain.user.dto.response;

import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;

import java.util.List;

public record UserUploadedReferenceListResponse(
        List<CommonReferenceInfo> userUploadedReferenceList
) {
}
