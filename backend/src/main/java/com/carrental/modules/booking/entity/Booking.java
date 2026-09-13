package com.carrental.modules.booking.entity;

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

@Entity
@Table(name = "bookings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {
    @Column(name = "booking_number", nullable = false, unique = true, length = 50)
    private String bookingNumber;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "car_id", nullable = false) private Long carId;
    @Column(name = "pickup_location_id", nullable = false) private Long pickupLocationId;
    @Column(name = "dropoff_location_id", nullable = false) private Long dropoffLocationId;
    @Column(name = "pickup_date", nullable = false) private LocalDateTime pickupDate;
    @Column(name = "dropoff_date", nullable = false) private LocalDateTime dropoffDate;
    @Column(name = "total_cost", precision = 10, scale = 2) private BigDecimal totalCost;
    @Enumerated(EnumType.STRING) @Column(name = "booking_status", nullable = false, length = 50) @Builder.Default private BookingStatus bookingStatus = BookingStatus.PENDING;
    private String notes;

    public enum BookingStatus { PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED }
}