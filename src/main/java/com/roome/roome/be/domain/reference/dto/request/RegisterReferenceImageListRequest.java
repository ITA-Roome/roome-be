package com.roome.roome.be.domain.reference.dto.request;


import java.util.List;

public record RegisterReferenceImageListRequest(
        Long referenceId,
        List<String> objectKeys
) {
}
