package com.carrental.modules.inspection.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_inspections",
        uniqueConstraints = @UniqueConstraint(name = "uq_vehicle_inspection_booking_type",
                columnNames = {"booking_id", "inspection_type"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleInspection extends BaseEntity {
    @Column(name = "booking_id", nullable = false) private Long bookingId;
    @Column(name = "car_id", nullable = false) private Long carId;
    @Enumerated(EnumType.STRING) @Column(name = "inspection_type", nullable = false, length = 20)
    private InspectionType inspectionType;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    @Builder.Default private InspectionStatus status = InspectionStatus.DRAFT;
    @Column(name = "inspected_by", nullable = false) private Long inspectedBy;
    private LocalDateTime inspectedAt;
    @Column(name = "odometer_reading", nullable = false) private Long odometerReading;
    @Column(name = "fuel_level", nullable = false, precision = 5, scale = 2) private BigDecimal fuelLevel;
    @Column(name = "exterior_condition", nullable = false, length = 30) private String exteriorCondition;
    @Column(name = "interior_condition", nullable = false, length = 30) private String interiorCondition;
    @Column(columnDefinition = "TEXT") private String notes;
    private LocalDateTime customerAcknowledgedAt;

    public enum InspectionType { PICKUP, RETURN }
    public enum InspectionStatus { DRAFT, COMPLETED }
}
