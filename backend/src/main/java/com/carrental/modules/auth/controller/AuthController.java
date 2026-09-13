package com.carrental.modules.auth.controller;

import com.carrental.modules.auth.dto.AuthResponse;
import com.carrental.modules.auth.dto.LoginRequest;
import com.carrental.modules.auth.dto.RefreshRequest;
import com.carrental.modules.auth.dto.RegisterRequest;
import com.carrental.modules.auth.dto.UserResponse;
import com.carrental.modules.auth.service.AuthService;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register") public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authService.register(request), "Registration successful")); }
    @PostMapping("/login") public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) { return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful")); }
    @PostMapping("/refresh") public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) { return ResponseEntity.ok(ApiResponse.success(authService.refresh(request), "Token refreshed successfully")); }
    @PostMapping("/logout") public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshRequest request) { authService.logout(request); return ResponseEntity.ok(ApiResponse.success(null, "Logout successful")); }
    @GetMapping("/me") public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) { return ResponseEntity.ok(ApiResponse.success(authService.getCurrentUser(Long.valueOf(authentication.getName())), "Current user retrieved successfully")); }
}