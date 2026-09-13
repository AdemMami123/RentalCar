package com.carrental.modules.auth.dto;

import com.carrental.modules.user.entity.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    String role;
    String phone;
    String licenseNumber;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRoles().stream().map(role -> role.getName()).findFirst().orElse(null))
                .phone(user.getPhone())
                .licenseNumber(user.getLicenseNumber())
                .build();
    }
}