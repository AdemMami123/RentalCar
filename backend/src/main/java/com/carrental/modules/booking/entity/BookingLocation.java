package com.carrental.modules.booking.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_locations",
        uniqueConstraints = @UniqueConstraint(name = "uq_booking_location_type",
                columnNames = {"booking_id", "location_type"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingLocation extends BaseEntity {
    @Column(name = "booking_id", nullable = false) private Long bookingId;
    @Enumerated(EnumType.STRING) @Column(name = "location_type", nullable = false, length = 20)
    private LocationType locationType;
    @Enumerated(EnumType.STRING) @Column(name = "fulfillment_type", nullable = false, length = 30)
    private FulfillmentType fulfillmentType;
    @Column(name = "rental_location_id") private Long rentalLocationId;
    @Column(nullable = false, length = 500) private String address;
    @Column(precision = 10, scale = 8) private BigDecimal latitude;
    @Column(precision = 11, scale = 8) private BigDecimal longitude;
    @Column(name = "delivery_instructions", length = 1000) private String deliveryInstructions;
    @Column(name = "scheduled_at", nullable = false) private LocalDateTime scheduledAt;
    @Column(nullable = false, length = 80) private String timezone;
    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2) private BigDecimal deliveryFee;

    public enum LocationType { PICKUP, RETURN }
    public enum FulfillmentType { OFFICE_PICKUP, DELIVERY }
}
