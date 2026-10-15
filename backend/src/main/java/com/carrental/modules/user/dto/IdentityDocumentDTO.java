package com.carrental.modules.user.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class IdentityDocumentDTO {
    Long id;
    String documentType;
    String originalFilename;
    String contentType;
    LocalDateTime uploadedAt;
}
