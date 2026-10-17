package com.carrental.modules.inspection.repository;

import com.carrental.modules.inspection.entity.InspectionDamage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InspectionDamageRepository extends JpaRepository<InspectionDamage, Long> {
    List<InspectionDamage> findByInspectionId(Long inspectionId);
}
