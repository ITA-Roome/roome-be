package com.roome.roome.be.domain.reference.dto.response;

import java.util.List;

public record ReferenceListResponse(
    List<CommonReferenceInfo> referenceList
) {
}
