package com.carrental.modules.payment.repository;

import com.carrental.modules.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByPaymentStatus(Payment.PaymentStatus status);
}