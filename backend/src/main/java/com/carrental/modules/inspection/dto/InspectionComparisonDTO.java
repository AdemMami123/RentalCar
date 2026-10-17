package com.carrental.modules.inspection.dto;

import java.math.BigDecimal;
import java.util.List;

public record InspectionComparisonDTO(
        Long bookingId, Long pickupOdometer, Long returnOdometer, Long distanceDriven,
        BigDecimal pickupFuelLevel, BigDecimal returnFuelLevel, BigDecimal fuelDifference,
        List<InspectionDTO.DamageDTO> existingDamage, List<InspectionDTO.DamageDTO> potentialNewDamage,
        boolean hasDiscrepancies) {}
