package com.carrental.modules.payment.service;

import com.carrental.modules.payment.dto.PaymentDTO;
import com.carrental.modules.payment.entity.Payment;
import com.carrental.modules.payment.mapper.PaymentMapper;
import com.carrental.modules.payment.repository.PaymentRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    public List<PaymentDTO> getAllPayments() { return paymentRepository.findAll().stream().map(paymentMapper::toDTO).toList(); }
    public PaymentDTO getPaymentById(Long id) { return paymentMapper.toDTO(paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id))); }
    public List<PaymentDTO> getPaymentsByBooking(Long bookingId) { return paymentRepository.findByBookingId(bookingId).stream().map(paymentMapper::toDTO).toList(); }
    @Transactional public PaymentDTO createPayment(PaymentDTO dto) { if (dto.getPaymentDate() == null) dto.setPaymentDate(java.time.LocalDateTime.now()); return paymentMapper.toDTO(paymentRepository.save(paymentMapper.toEntity(dto))); }
    @Transactional public PaymentDTO updatePayment(Long id, PaymentDTO dto) { Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id)); paymentMapper.updateEntityFromDTO(dto, payment); return paymentMapper.toDTO(paymentRepository.save(payment)); }
    @Transactional public void deletePayment(Long id) { Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id)); paymentRepository.delete(payment); }
}