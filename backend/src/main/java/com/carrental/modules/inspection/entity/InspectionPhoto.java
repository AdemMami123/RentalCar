package com.carrental.modules.inspection.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inspection_photos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InspectionPhoto extends BaseEntity {
    @Column(name = "inspection_id", nullable = false) private Long inspectionId;
    @Column(name = "damage_id") private Long damageId;
    @Column(name = "photo_url", length = 1000) private String photoUrl;
    @Column(name = "storage_key", nullable = false, length = 255) private String storageKey;
    @Column(nullable = false, length = 30) private String category;
    @Column(length = 500) private String description;
    @Column(name = "uploaded_at", nullable = false) private LocalDateTime uploadedAt;
}
