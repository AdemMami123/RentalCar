package com.carrental.modules.user.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String password;

    private String firstName;
    private String lastName;
    private String phone;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String address;
    private String city;
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}