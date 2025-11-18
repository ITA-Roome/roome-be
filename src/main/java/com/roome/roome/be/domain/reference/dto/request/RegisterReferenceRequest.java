package com.roome.roome.be.domain.reference.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record RegisterReferenceRequest(
        List<MultipartFile> files
) {
}
