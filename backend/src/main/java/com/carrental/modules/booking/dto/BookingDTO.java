package com.carrental.modules.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingDTO {
    private Long id;
    private String bookingNumber;
    @NotNull private Long userId;
    @NotNull private Long carId;
    @NotNull private Long pickupLocationId;
    @NotNull private Long dropoffLocationId;
    @NotNull private LocalDateTime pickupDate;
    @NotNull private LocalDateTime dropoffDate;
    private Double totalCost;
    private String bookingStatus;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}