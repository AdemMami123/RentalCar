package com.carrental.modules.user.controller;

import com.carrental.modules.user.dto.UserDTO;
import com.carrental.modules.user.service.UserService;
import com.carrental.shared.dtos.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> uploadProfileImage(
            @RequestPart("image") MultipartFile image,
            Authentication authentication) {
        UserDTO user = userService.updateProfileImage(Long.valueOf(authentication.getName()), image);
        return ResponseEntity.ok(ApiResponse.success(user, "Profile image updated successfully"));
    }
}