package com.carrental.modules.user.service;

import com.carrental.modules.user.dto.IdentityDocumentDTO;
import com.carrental.modules.user.entity.IdentityDocument;
import com.carrental.modules.user.entity.User;
import com.carrental.modules.user.repository.IdentityDocumentRepository;
import com.carrental.modules.user.repository.UserRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import com.carrental.shared.services.CloudinaryMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IdentityDocumentService {
    private static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_TYPES = List.of("application/pdf", "image/jpeg", "image/png");

    private final IdentityDocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CloudinaryMediaService mediaService;

    @Transactional
    public IdentityDocumentDTO upload(Long userId, IdentityDocument.DocumentType type, MultipartFile file) {
        validate(file);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CloudinaryMediaService.UploadedMedia uploaded = mediaService.uploadPrivateDocument(
                file, "rentalcar/documents/" + userId);
        IdentityDocument document = documentRepository.save(IdentityDocument.builder()
                .user(user)
                .documentType(type)
                .publicId(uploaded.publicId())
                .originalFilename(safeFilename(file.getOriginalFilename()))
                .contentType(file.getContentType())
                .build());
        return toDTO(document);
    }

    @Transactional(readOnly = true)
    public List<IdentityDocumentDTO> list(Long userId) {
        if (!userRepository.existsById(userId)) throw new ResourceNotFoundException("User not found");
        return documentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public String previewUrl(Long userId, Long documentId) {
        IdentityDocument document = documentRepository.findById(documentId)
                .filter(item -> Objects.equals(item.getUser().getId(), userId))
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        return mediaService.createPrivateDocumentUrl(document.getPublicId(), document.getContentType());
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("A document file is required");
        if (file.getSize() > MAX_DOCUMENT_SIZE) throw new IllegalArgumentException("Documents must be 10 MB or smaller");
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType().toLowerCase())) {
            throw new IllegalArgumentException("Only PDF, JPEG, and PNG documents are allowed");
        }
    }

    private String safeFilename(String filename) {
        String value = filename == null ? "document" : filename.replace('\\', '/');
        value = value.substring(value.lastIndexOf('/') + 1);
        return value.length() > 255 ? value.substring(value.length() - 255) : value;
    }

    private IdentityDocumentDTO toDTO(IdentityDocument document) {
        return IdentityDocumentDTO.builder()
                .id(document.getId())
                .documentType(document.getDocumentType().name())
                .originalFilename(document.getOriginalFilename())
                .contentType(document.getContentType())
                .uploadedAt(document.getCreatedAt())
                .build();
    }
}
