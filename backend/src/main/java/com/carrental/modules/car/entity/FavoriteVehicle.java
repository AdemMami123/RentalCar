package com.carrental.modules.car.entity;

import com.carrental.shared.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite_vehicles", uniqueConstraints = @UniqueConstraint(name = "uq_favorite_vehicle_user_car", columnNames = {"user_id", "car_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteVehicle extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "car_id", nullable = false)
    private Long carId;
}
