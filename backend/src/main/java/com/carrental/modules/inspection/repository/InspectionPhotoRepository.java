package com.carrental.modules.inspection.repository;

import com.carrental.modules.inspection.entity.InspectionPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InspectionPhotoRepository extends JpaRepository<InspectionPhoto, Long> {
    List<InspectionPhoto> findByInspectionId(Long inspectionId);
}
