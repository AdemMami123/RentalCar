package com.carrental.modules.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentDTO {
    private Long id;
    @NotNull private Long bookingId;
    @NotNull @Positive private Double amount;
    @NotNull private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}