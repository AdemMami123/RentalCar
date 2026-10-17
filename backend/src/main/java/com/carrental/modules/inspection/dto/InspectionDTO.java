package com.carrental.modules.inspection.dto;

import com.carrental.modules.inspection.entity.VehicleInspection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InspectionDTO(
        Long id, @NotNull Long bookingId, Long carId,
        @NotNull VehicleInspection.InspectionType inspectionType,
        VehicleInspection.InspectionStatus status, Long inspectedBy, LocalDateTime inspectedAt,
        @NotNull @PositiveOrZero Long odometerReading,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal fuelLevel,
        @NotBlank String exteriorCondition, @NotBlank String interiorCondition, String notes,
        LocalDateTime customerAcknowledgedAt, List<@Valid DamageDTO> damages, List<PhotoDTO> photos) {
    public record DamageDTO(Long id, @NotBlank String damageType, @NotBlank String locationOnVehicle,
                            @NotBlank String description, @NotBlank String severity) {}
    public record PhotoDTO(Long id, Long damageId, String photoUrl, String storageKey,
                           @NotBlank String category, String description, LocalDateTime uploadedAt) {}
}
