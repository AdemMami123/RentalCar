package com.carrental.modules.payment.controller;

import com.carrental.modules.payment.dto.PaymentDTO;
import com.carrental.modules.payment.service.PaymentService;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/payments") @RequiredArgsConstructor @CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAll() { return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments(), "Payments retrieved successfully")); }
    @GetMapping("/{id}") public ResponseEntity<ApiResponse<PaymentDTO>> getById(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(id), "Payment retrieved successfully")); }
    @GetMapping("/booking/{bookingId}") public ResponseEntity<ApiResponse<List<PaymentDTO>>> getByBooking(@PathVariable Long bookingId) { return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByBooking(bookingId), "Payments retrieved successfully")); }
    @PostMapping public ResponseEntity<ApiResponse<PaymentDTO>> create(@Valid @RequestBody PaymentDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(paymentService.createPayment(dto), "Payment created successfully")); }
    @PutMapping("/{id}") public ResponseEntity<ApiResponse<PaymentDTO>> update(@PathVariable Long id, @Valid @RequestBody PaymentDTO dto) { return ResponseEntity.ok(ApiResponse.success(paymentService.updatePayment(id, dto), "Payment updated successfully")); }
    @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) { paymentService.deletePayment(id); return ResponseEntity.ok(ApiResponse.success(null, "Payment deleted successfully")); }
}