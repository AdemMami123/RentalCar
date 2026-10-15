package com.carrental.modules.user.controller;

import com.carrental.modules.user.dto.UserDTO;
import com.carrental.modules.user.service.UserService;
import com.carrental.shared.dtos.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final com.carrental.modules.user.service.IdentityDocumentService documentService;

    @PutMapping
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody UserDTO profile,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.updateOwnProfile(Long.valueOf(authentication.getName()), profile),
                "Profile updated successfully"));
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> uploadProfileImage(
            @RequestPart("image") MultipartFile image,
            Authentication authentication) {
        UserDTO user = userService.updateProfileImage(Long.valueOf(authentication.getName()), image);
        return ResponseEntity.ok(ApiResponse.success(user, "Profile image updated successfully"));
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<com.carrental.modules.user.dto.IdentityDocumentDTO>> uploadDocument(
            @RequestParam("type") com.carrental.modules.user.entity.IdentityDocument.DocumentType type,
            @RequestPart("document") MultipartFile document,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(documentService.upload(
                Long.valueOf(authentication.getName()), type, document), "Document uploaded securely"));
    }

    @GetMapping("/documents")
    public ResponseEntity<ApiResponse<java.util.List<com.carrental.modules.user.dto.IdentityDocumentDTO>>> listDocuments(
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.list(Long.valueOf(authentication.getName())), "Documents retrieved successfully"));
    }
}