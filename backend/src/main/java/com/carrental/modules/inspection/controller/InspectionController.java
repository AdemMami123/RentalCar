package com.carrental.modules.inspection.controller;

import com.carrental.modules.inspection.dto.InspectionComparisonDTO;
import com.carrental.modules.inspection.dto.InspectionDTO;
import com.carrental.modules.inspection.service.InspectionService;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200")
public class InspectionController {
    private final InspectionService inspectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<InspectionDTO>> create(@Valid @RequestBody InspectionDTO request,
                                                              Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(inspectionService.createDraft(request, userId(authentication)), "Inspection draft created successfully"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<InspectionDTO>> complete(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                inspectionService.completeDraft(id, userId(authentication)), "Inspection completed successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionDTO>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(inspectionService.getInspection(id), "Inspection retrieved successfully"));
    }

    @GetMapping("/bookings/{bookingId}/comparison")
    public ResponseEntity<ApiResponse<InspectionComparisonDTO>> comparison(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success(inspectionService.compare(bookingId), "Inspection comparison generated successfully"));
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<InspectionDTO>> uploadPhoto(
            @PathVariable Long id, @RequestPart("file") MultipartFile file,
            @RequestParam String category, @RequestParam(required = false) String description,
            @RequestParam(required = false) Long damageId,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                inspectionService.uploadPhoto(id, category, description, file, damageId, userId(authentication)),
                "Inspection photo uploaded successfully"));
    }

    private Long userId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authenticated inspector is required");
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Authenticated user identity is invalid", exception);
        }
    }
}
