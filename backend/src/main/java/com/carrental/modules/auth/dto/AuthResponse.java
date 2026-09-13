package com.carrental.modules.auth.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    long expiresIn;
    UserResponse user;
}