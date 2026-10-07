package com.carrental.modules.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCustomerDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String address;
    private String city;
    private String country;
}
