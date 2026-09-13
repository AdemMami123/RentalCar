package com.carrental.modules.payment.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "payments") @Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {
    @Column(name = "booking_id", nullable = false) private Long bookingId;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(name = "payment_method", nullable = false, length = 50) private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING) @Column(name = "payment_status", nullable = false, length = 50) @Builder.Default private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "transaction_id", unique = true, length = 100) private String transactionId;
    @Column(name = "payment_date") private LocalDateTime paymentDate;
    public enum PaymentMethod { CREDIT_CARD, DEBIT_CARD, CASH, BANK_TRANSFER, PAYPAL }
    public enum PaymentStatus { PENDING, COMPLETED, FAILED, REFUNDED }
}