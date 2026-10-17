package com.carrental.modules.inspection.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspection_damages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InspectionDamage extends BaseEntity {
    @Column(name = "inspection_id", nullable = false) private Long inspectionId;
    @Column(name = "damage_type", nullable = false, length = 50) private String damageType;
    @Column(name = "location_on_vehicle", nullable = false, length = 100) private String locationOnVehicle;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    @Column(nullable = false, length = 20) private String severity;
}
