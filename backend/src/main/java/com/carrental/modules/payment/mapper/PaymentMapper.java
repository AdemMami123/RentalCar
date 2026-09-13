package com.carrental.modules.payment.mapper;

import com.carrental.modules.payment.dto.PaymentDTO;
import com.carrental.modules.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {
    @Mapping(target = "amount", expression = "java(payment.getAmount() != null ? payment.getAmount().doubleValue() : null)")
    @Mapping(target = "paymentMethod", expression = "java(payment.getPaymentMethod() != null ? payment.getPaymentMethod().toString() : null)")
    @Mapping(target = "paymentStatus", expression = "java(payment.getPaymentStatus() != null ? payment.getPaymentStatus().toString() : null)")
    PaymentDTO toDTO(Payment payment);
    @Mapping(target = "amount", expression = "java(dto.getAmount() != null ? java.math.BigDecimal.valueOf(dto.getAmount()) : null)")
    @Mapping(target = "paymentMethod", expression = "java(dto.getPaymentMethod() != null ? Payment.PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase()) : null)")
    @Mapping(target = "paymentStatus", expression = "java(dto.getPaymentStatus() != null ? Payment.PaymentStatus.valueOf(dto.getPaymentStatus().toUpperCase()) : Payment.PaymentStatus.PENDING)")
    Payment toEntity(PaymentDTO dto);
    @Mapping(target = "amount", expression = "java(dto.getAmount() != null ? java.math.BigDecimal.valueOf(dto.getAmount()) : payment.getAmount())")
    @Mapping(target = "paymentMethod", expression = "java(dto.getPaymentMethod() != null ? Payment.PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase()) : payment.getPaymentMethod())")
    @Mapping(target = "paymentStatus", expression = "java(dto.getPaymentStatus() != null ? Payment.PaymentStatus.valueOf(dto.getPaymentStatus().toUpperCase()) : payment.getPaymentStatus())")
    void updateEntityFromDTO(PaymentDTO dto, @MappingTarget Payment payment);
}