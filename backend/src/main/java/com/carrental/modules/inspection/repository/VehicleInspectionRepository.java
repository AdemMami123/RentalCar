package com.carrental.modules.inspection.repository;

import com.carrental.modules.inspection.entity.VehicleInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehicleInspectionRepository extends JpaRepository<VehicleInspection, Long> {
    Optional<VehicleInspection> findByBookingIdAndInspectionType(Long bookingId, VehicleInspection.InspectionType type);
}
